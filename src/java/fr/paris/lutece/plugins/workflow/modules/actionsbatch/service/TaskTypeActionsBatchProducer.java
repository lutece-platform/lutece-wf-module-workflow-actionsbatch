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

import org.eclipse.microprofile.config.inject.ConfigProperty;

import fr.paris.lutece.plugins.workflow.business.task.TaskTypeBuilder;
import fr.paris.lutece.plugins.workflowcore.business.task.ITaskType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

/**
 * CDI producer for the actions batch task type.
 */
@ApplicationScoped
public class TaskTypeActionsBatchProducer
{
    /**
     * Produces the task type of the actions batch task.
     *
     * @param key
     *            the task type key
     * @param titleI18nKey
     *            the i18n key of the task type title
     * @param beanName
     *            the name of the task bean
     * @param configBeanName
     *            the name of the task configuration bean
     * @param configRequired
     *            true if the task requires a configuration
     * @param formTaskRequired
     *            true if the task requires a form
     * @param taskForAutomaticAction
     *            true if the task can be used by an automatic action
     * @return the task type
     */
    @Produces
    @ApplicationScoped
    @Named( "workflow-actionsbatch.actionsBatchTaskType" )
    public ITaskType produceActionsBatchTaskType(
            @ConfigProperty( name = "workflow-actionsbatch.actionsBatchTaskType.key" ) String key,
            @ConfigProperty( name = "workflow-actionsbatch.actionsBatchTaskType.titleI18nKey" ) String titleI18nKey,
            @ConfigProperty( name = "workflow-actionsbatch.actionsBatchTaskType.beanName" ) String beanName,
            @ConfigProperty( name = "workflow-actionsbatch.actionsBatchTaskType.configBeanName" ) String configBeanName,
            @ConfigProperty( name = "workflow-actionsbatch.actionsBatchTaskType.configRequired", defaultValue = "false" ) boolean configRequired,
            @ConfigProperty( name = "workflow-actionsbatch.actionsBatchTaskType.formTaskRequired", defaultValue = "false" ) boolean formTaskRequired,
            @ConfigProperty( name = "workflow-actionsbatch.actionsBatchTaskType.taskForAutomaticAction", defaultValue = "false" ) boolean taskForAutomaticAction )
    {
        return TaskTypeBuilder.buildTaskType( key, titleI18nKey, beanName, configBeanName, configRequired, formTaskRequired, taskForAutomaticAction );
    }
}
