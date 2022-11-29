
DROP TABLE IF EXISTS workflow_task_actions_batch_cf;
CREATE TABLE workflow_task_actions_batch_cf (
	id_task int NOT NULL,
	id_workflow INT NOT NULL,
	id_state INT NOT NULL,
	id_action INT NOT NULL,
	resource_type VARCHAR(255) NOT NULL,
	CONSTRAINT wworkflow_task_actions_batch_cf_pkey PRIMARY KEY (id_task)
);