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

import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfigDAO;
import fr.paris.lutece.util.sql.DAOUtil;

public class ActionsBatchTaskConfigDAO implements ITaskConfigDAO<ActionsBatchTaskConfig>
{

    // Constants
    private static final String SQL_QUERY_SELECT = "SELECT id_task, id_workflow, id_state, id_action, resource_type FROM workflow_task_actions_batch_cf WHERE id_task = ?";
    private static final String SQL_QUERY_INSERT = "INSERT INTO workflow_task_actions_batch_cf ( id_task, id_workflow, id_state, id_action, resource_type ) VALUES ( ?, ?, ?, ?, ? ) ";
    private static final String SQL_QUERY_DELETE = "DELETE FROM workflow_task_actions_batch_cf WHERE id_task = ? ";
    private static final String SQL_QUERY_UPDATE = "UPDATE workflow_task_actions_batch_cf SET id_workflow = ?, id_state = ?, id_action = ?, resource_type = ? WHERE id_task = ?";

    @Override
    public void insert( ActionsBatchTaskConfig config )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT ) )
        {
            daoUtil.setInt( 1, config.getIdTask( ) );
            daoUtil.setInt( 2, config.getIdWorkflow( ) );
            daoUtil.setInt( 3, config.getIdState( ) );
            daoUtil.setInt( 4, config.getIdAction( ) );
            daoUtil.setString( 5, config.getResourceType( ) );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public void store( ActionsBatchTaskConfig config )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE ) )
        {
            daoUtil.setInt( 1, config.getIdWorkflow( ) );
            daoUtil.setInt( 2, config.getIdState( ) );
            daoUtil.setInt( 3, config.getIdAction( ) );
            daoUtil.setInt( 4, config.getIdTask( ) );
            daoUtil.setString( 5, config.getResourceType( ) );
            daoUtil.executeUpdate( );
        }
    }

    @Override
    public ActionsBatchTaskConfig load( int nIdTask )
    {

        ActionsBatchTaskConfig batchActionsTaskConfig = null;

        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeQuery( );
            if ( daoUtil.next( ) )
            {
                batchActionsTaskConfig = new ActionsBatchTaskConfig( );
                int i = 1;
                batchActionsTaskConfig.setIdTask( daoUtil.getInt( i++ ) );
                batchActionsTaskConfig.setIdWorkflow( daoUtil.getInt( i++ ) );
                batchActionsTaskConfig.setIdState( daoUtil.getInt( i++ ) );
                batchActionsTaskConfig.setIdAction( daoUtil.getInt( i++ ) );
                batchActionsTaskConfig.setResourceType( daoUtil.getString( i ) );
            }
        }

        return batchActionsTaskConfig;
    }

    @Override
    public void delete( int nIdTask )
    {
        try ( DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE ) )
        {
            daoUtil.setInt( 1, nIdTask );
            daoUtil.executeUpdate( );
        }
    }

}
