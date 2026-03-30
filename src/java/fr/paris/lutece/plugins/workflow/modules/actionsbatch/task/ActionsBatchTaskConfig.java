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

import javax.validation.constraints.NotNull;

import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

public class ActionsBatchTaskConfig extends TaskConfig
{
    // Variables declarations
    @NotNull
    private int _nIdWorkflow;
    @NotNull
    private int _nIdState;
    @NotNull
    private int _nIdAction;
    @NotNull
    private String _strResourceType;

    /**
     * Returns the IdWorkflow
     * 
     * @return The IdWorkflow
     */
    public int getIdWorkflow( )
    {
        return _nIdWorkflow;
    }

    /**
     * Sets the IdWorkflow
     * 
     * @param nIdWorkflow
     *            The IdWorkflow
     */
    public void setIdWorkflow( int nIdWorkflow )
    {
        _nIdWorkflow = nIdWorkflow;
    }

    /**
     * Returns the IdState
     * 
     * @return The IdState
     */
    public int getIdState( )
    {
        return _nIdState;
    }

    /**
     * Sets the IdState
     * 
     * @param nIdState
     *            The IdState
     */
    public void setIdState( int nIdState )
    {
        _nIdState = nIdState;
    }

    /**
     * Returns the IdAction
     * 
     * @return The IdAction
     */
    public int getIdAction( )
    {
        return _nIdAction;
    }

    /**
     * Sets the IdAction
     * 
     * @param nIdAction
     *            The IdAction
     */
    public void setIdAction( int nIdAction )
    {
        _nIdAction = nIdAction;
    }

    /**
     * get resource type
     * 
     * @return the resource type
     */
    public String getResourceType( )
    {

        return _strResourceType;
    }

    /**
     * set resource type
     * 
     * @param strResourceType
     */
    public void setResourceType( String strResourceType )
    {
        this._strResourceType = strResourceType;
    }



}
