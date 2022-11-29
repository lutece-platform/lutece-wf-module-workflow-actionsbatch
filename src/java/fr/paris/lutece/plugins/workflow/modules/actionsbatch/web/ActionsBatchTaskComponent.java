/*
 * Copyright (c) 2002-2022, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.actionsbatch.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.workflow.modules.actionsbatch.task.ActionsBatchTaskConfig;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.business.action.Action;
import fr.paris.lutece.plugins.workflowcore.business.action.ActionFilter;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.business.state.State;
import fr.paris.lutece.plugins.workflowcore.service.action.ActionService;
import fr.paris.lutece.plugins.workflowcore.service.action.IActionService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.message.AdminMessage;
import fr.paris.lutece.portal.service.message.AdminMessageService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import fr.paris.lutece.portal.web.constants.Messages;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.beanvalidation.BeanValidationUtil;
import fr.paris.lutece.util.html.HtmlTemplate;

/**
 * ActionsBatchTaskComponent Class
 * 
 * @author MDP,ACN
 *
 */
public class ActionsBatchTaskComponent extends NoFormTaskComponent
{
    // MARKERS
    private static final String MARK_WORKFLOWS = "workflow_list";
    private static final String MARK_STATUS = "status_list";
    private static final String MARK_ACTION = "action_list";
    private static final String MARK_RESOURCE_TYPE = "resource_type";

    // PARAMETERS
    private static final String PARAM_STATUS = "state";
    private static final String PARAM_ACTION = "action";
    private static final String PARAM_WORKFLOW = "workflow";
    private static final String PARAM_RESOURCE_TYPE = "resource_type";

    // TEMPLATES
    private static final String TEMPLATE_TASK_DEMANDE_MASS_ACTION_CONFIG = "admin/plugins/workflow/modules/actionsbatch/actionsbatch_task_config.html";
    private static final String MARK_WORKFLOW_ACTIONS = "json_workflow_actions";
    private static final String MARK_WORKFLOW_ID = "workflow_id";
    private static final String MARK_STATE_ID = "state_id";
    private static final String MARK_ACTION_ID = "action_id";

    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
    {
        User user = AdminUserService.getAdminUser( request );
        if ( user == null )
        {
            throw new AppException( "Access Denied" );
        }

        // service
        IActionService _actionService = SpringContextService.getBean( ActionService.BEAN_SERVICE );

        // model
        final Map<String, Object> model = new HashMap<>( );

        // get config
        final ActionsBatchTaskConfig config = findTaskConfig( task.getId( ) );

        // get enabled workflow list
        final ReferenceList workflowsRefList = WorkflowService.getInstance( ).getWorkflowsEnabled( user, locale );

        // remove first blank item
        workflowsRefList.remove( 0 );

        // get all available states and actions
        Map<String, Collection<State>> mapStates = new HashMap<>( );
        Map<String, List<Action>> mapActions = new HashMap<>( );

        for ( ReferenceItem workflowItem : workflowsRefList )
        {
            // STATES
            Collection<State> workflowStates = WorkflowService.getInstance( ).getAllStateByWorkflow( Integer.valueOf( workflowItem.getCode( ) ), user );

            // put in global map
            mapStates.put( workflowItem.getCode( ), workflowStates );

            // ACTIONS
            for ( State state : workflowStates )
            {

                // get actions
                final ActionFilter filter = new ActionFilter( );
                filter.setIdStateBefore( state.getId( ) );
                final List<Action> stateActions = _actionService.getListActionByFilter( filter );

                // put in map
                mapActions.put( String.valueOf( state.getId( ) ), stateActions );
            }
        }

        model.put( MARK_WORKFLOW_ACTIONS, getJsonActions( workflowsRefList, mapStates, mapActions ) );

        model.put( MARK_WORKFLOW_ID, config.getIdWorkflow( ) );
        model.put( MARK_STATE_ID, config.getIdState( ) );
        model.put( MARK_ACTION_ID, config.getIdAction( ) );
        model.put( MARK_RESOURCE_TYPE, config.getResourceType( ) );

        final HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_DEMANDE_MASS_ACTION_CONFIG, locale, model );
        return template.getHtml( );
    }

    @Override
    public String validateConfig( ITaskConfig config, HttpServletRequest request )
    {
        String workflow = request.getParameter( PARAM_WORKFLOW );
        String status = request.getParameter( PARAM_STATUS );
        String action = request.getParameter( PARAM_ACTION );
        String resourceType = request.getParameter( PARAM_RESOURCE_TYPE );

        if ( StringUtils.isBlank( workflow ) || StringUtils.isBlank( status ) || StringUtils.isBlank( action ) || StringUtils.isBlank( resourceType ) )
        {
            return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
        }
        else
            if ( config instanceof ActionsBatchTaskConfig )
            {
                final ActionsBatchTaskConfig taskConfig = (ActionsBatchTaskConfig) config;
                taskConfig.setIdWorkflow( Integer.parseInt( workflow ) );
                taskConfig.setIdState( Integer.parseInt( status ) );
                taskConfig.setIdAction( Integer.parseInt( action ) );
                taskConfig.setResourceType( resourceType );

                // Check mandatory fields
                Set<ConstraintViolation<ITaskConfig>> constraintViolations = BeanValidationUtil.validate( taskConfig );

                if ( CollectionUtils.isNotEmpty( constraintViolations ) )
                {
                    return AdminMessageService.getMessageUrl( request, Messages.MANDATORY_FIELDS, AdminMessage.TYPE_STOP );
                }
            }

        return StringUtils.EMPTY;
    }

    /**
     * get config
     * 
     * @param id
     *            id Config
     * @return ActionsBatchTaskConfig
     */
    private ActionsBatchTaskConfig findTaskConfig( int id )
    {
        final ActionsBatchTaskConfig config = this.getTaskConfigService( ).findByPrimaryKey( id );
        return config == null ? new ActionsBatchTaskConfig( ) : config;
    }

    @Override
    public String getDisplayTaskInformation( int pNIdHistory, HttpServletRequest pRequest, Locale pLocale, ITask pTask )
    {
        return null;
    }

    /**
     * generate Json for cascade selects in template
     * 
     * example : {"workflows":[{"id":1, "name":"dotation", "states":[{"id":12, "name":"statut11", "actions":[{"id":111, "name": "action111"}, ...
     * 
     * @param workflowsRefList
     * @param mapStates
     * @param mapActions
     * @return json
     */
    private String getJsonActions( ReferenceList workflowsRefList, Map<String, Collection<State>> mapStates, Map<String, List<Action>> mapActions )
    {
        ObjectMapper mapper = new ObjectMapper( );
        ObjectNode root = mapper.createObjectNode( );

        ArrayNode jsonWfList = mapper.createArrayNode( );
        for ( ReferenceItem workflowItem : workflowsRefList )
        {
            ObjectNode jsonWf = mapper.createObjectNode( );
            jsonWf.put( "id", workflowItem.getCode( ) );
            jsonWf.put( "name", workflowItem.getName( ) );

            ArrayNode jsonStatesList = mapper.createArrayNode( );
            for ( State state : mapStates.get( workflowItem.getCode( ) ) )
            {
                ObjectNode jsonState = mapper.createObjectNode( );
                jsonState.put( "id", state.getId( ) );
                jsonState.put( "name", state.getName( ) );

                ArrayNode jsonActionsList = mapper.createArrayNode( );
                for ( Action action : mapActions.get( String.valueOf( state.getId( ) ) ) )
                {
                    ObjectNode jsonAction = mapper.createObjectNode( );
                    jsonAction.put( "id", action.getId( ) );
                    jsonAction.put( "name", action.getName( ) );

                    jsonActionsList.add( jsonAction );
                }

                jsonState.set( "actions", jsonActionsList );
                jsonStatesList.add( jsonState );
            }

            jsonWf.set( "states", jsonStatesList );
            jsonWfList.add( jsonWf );
        }

        root.set( "workflows", jsonWfList );

        return root.toPrettyString( );
    }
}
