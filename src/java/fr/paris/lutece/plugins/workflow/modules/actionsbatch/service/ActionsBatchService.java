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
package fr.paris.lutece.plugins.workflow.modules.actionsbatch.service;

import java.util.List;
import java.util.Locale;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.api.user.User;
import fr.paris.lutece.portal.service.util.AppException;
import fr.paris.lutece.portal.service.util.AppLogService;
import fr.paris.lutece.portal.service.workflow.WorkflowService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.servlet.http.HttpServletRequest;

@ApplicationScoped
public class ActionsBatchService
{
    @Inject
    private WorkflowService _workflowService;

    @Inject
    @ConfigProperty( name = "workflow-actionsbatch.pause.ms", defaultValue = "-1" )
    private int _nBatchPause;

    /**
     * Process actions in batch mode
     *
     * @param request
     * @param strResourceType
     * @param nIdAction
     * @param nParentResourceId
     * @param locale
     * @param user
     * @param listResourceIds
     * @param bIsAutomatic
     *
     *
     */
    public void doProcessMassActions( HttpServletRequest request, String strResourceType, int nIdAction, int nParentResourceId, Locale locale,
            User user, List<Integer> listResourceIds, boolean bIsAutomatic )
    {
        if ( listResourceIds.isEmpty( ) )
        {
            return ;
        }

        Runnable task = ( ) -> {
            for ( Integer nIdResource : listResourceIds )
            {
                try
                {
                    _workflowService.doProcessAction( nIdResource, strResourceType, nIdAction, nParentResourceId, request, locale, bIsAutomatic,
                            user );
                    if ( _nBatchPause > 0 )
                    {
                        try
                        {
                            Thread.sleep( _nBatchPause );
                            AppLogService.info( "Pause time between each action in MS" );
                        }
                        catch( InterruptedException e )
                        {
                            Thread.currentThread( ).interrupt( );
                            AppLogService.error( "An error occured while sleeping", e );
                        }
                    }
                }
                catch( AppException e )
                {
                    AppLogService.error( "An error occured when processing action {} on resource Id : {}", nIdAction, nIdResource );
                }
            }
        };
        
        Thread thread = new Thread( task );
        thread.start( );
    }

}
