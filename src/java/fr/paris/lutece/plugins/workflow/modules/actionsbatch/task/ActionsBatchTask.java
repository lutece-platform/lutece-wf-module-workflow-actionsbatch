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
package fr.paris.lutece.plugins.workflow.modules.actionsbatch.task;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.plugins.workflow.modules.actionsbatch.service.ActionsBatchService;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceHistoryService;
import fr.paris.lutece.portal.service.admin.AdminAuthenticationService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.progressmanager.ProgressManagerService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;


public class ActionsBatchTask extends AsynchronousSimpleTask
{
    private static final String PARAMETER_USER_ID = "userId";

    // Constants
    private static final String TASK_TITLE = "module.workflow.actionsbatch.title";

    // Services
    private static final IResourceHistoryService _resourceHistoryService = SpringContextService.getBean( ResourceHistoryService.BEAN_SERVICE );
    private static final ITaskConfigService _taskConfigService = SpringContextService.getBean( "workflow-actionsbatch.actionsBatchTaskConfigService" );
    private static final WorkflowService _workflowService = WorkflowService.getInstance( );
    private static final ProgressManagerService _progressManagerService = ProgressManagerService.getInstance( );

    // params
    private static final String PARAMETER_FEED_TOKEN = "FEED_TOKEN";

    @Override
    public void processAsynchronousTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        // Get resource id as parent ID for processiing child actions
        final ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        int parentId = resourceHistory.getId( );

        // get config
        ActionsBatchTaskConfig config = _taskConfigService.findByPrimaryKey( getId( ) );

        if ( config != null && config.getIdAction( ) > 0 && config.getIdState( ) > 0 && !StringUtils.isBlank( config.getResourceType( ) ) )
        {

            // get resource Ids
            List<Integer> listResourceIds = _workflowService.getResourceIdListByIdState( config.getIdState( ), config.getResourceType( ) );

            // get user
            User adminUser = AdminAuthenticationService.getInstance( ).getRegisteredUser( request );

            // get progress bar feed token and init feed
            // (the progress feed must be registered upstream with ProgressManagerService.register( ...) 
            // and set in the session to be used from the initial context in a template)
            String strFeedToken = (String) request.getSession( ).getAttribute ( getFeedToken( ) );
            _progressManagerService.initFeed( strFeedToken, listResourceIds.size( ) );

            if ( CollectionUtils.isNotEmpty( listResourceIds ) )
            {
                ActionsBatchService.doProcessMassActions( request, config.getResourceType( ), config.getIdAction( ), resourceHistory.getIdResource( ), locale,
                        adminUser, listResourceIds, true, strFeedToken );
            }
            else
            {
                // unregister
                _progressManagerService.unRegisterFeed( strFeedToken );
            }
        }

    }

    @Override
    public String getTitle( Locale pLocale )
    {
        return I18nService.getLocalizedString( TASK_TITLE, pLocale );
    }

    /**
     * get the feed token
     * @return the token
     */
    private String getFeedToken( )
    {
            return new StringBuilder( "FEED-")
            		.append( getAction( ).getResourceTypeCode( ) ).append( "-" )
            		.append( getAction( ).getResourceId( ) ).append( "-" )
            		.append( getAction( ).getWorkflow( ).getId( ) ).append( "-" )
            		.append( getAction( ).getId( ) )
                    .toString( );
    }
}
