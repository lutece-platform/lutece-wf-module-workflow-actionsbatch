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
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.AsynchronousSimpleTask;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;


public class ActionsBatchTask extends AsynchronousSimpleTask
{

    // Constants
    private static final String TASK_TITLE = "module.workflow.actionsbatch.title";

    // Services
    private static final ITaskConfigService _taskConfigService = SpringContextService.getBean( "workflow-actionsbatch.actionsBatchTaskConfigService" );
    private static final WorkflowService _workflowService = WorkflowService.getInstance( );


    @Override
    public void processAsynchronousTask( int parentId, String strResourceType, int nIdResourceHistory, HttpServletRequest request, Locale locale, User user )
    {
        // get config
        ActionsBatchTaskConfig config = _taskConfigService.findByPrimaryKey( getId( ) );

        if ( config != null && config.getIdAction( ) > 0 && config.getIdState( ) > 0 && !StringUtils.isBlank( config.getResourceType( ) ) )
        {

            // get resource Ids
            List<Integer> listResourceIds = _workflowService.getResourceIdListByIdState( config.getIdState( ), config.getResourceType( ), parentId );

            if ( CollectionUtils.isNotEmpty( listResourceIds ) )
            {
                ActionsBatchService.doProcessMassActions( request, config.getResourceType( ), config.getIdAction( ), parentId, locale,
                        user, listResourceIds, true );
            }
        }

    }

    @Override
    public String getTitle( Locale pLocale )
    {
        return I18nService.getLocalizedString( TASK_TITLE, pLocale );
    }


}
