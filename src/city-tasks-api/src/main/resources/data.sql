INSERT INTO HIP_CTY_TASKS (id, name, description, status, job_id, device_id, device_operation, task_hour, task_minute, execution_days, execute_until)
VALUES (101, 'Task 101', 'Task 101 Description', 'ACT', 'abcd', '123', 'ACTIVATE', 12, 0, 'MON,WED,FRI', '2025-01-01 00:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO HIP_CTY_TASKS (id, name, description, status, job_id, device_id, device_operation, task_hour, task_minute, execution_days, execute_until)
VALUES (102, 'Task 102', 'Task 102 Description', 'INA', 'abcd', '456', 'ACTIVATE', 12, 30, 'TUE,FRI', '2026-01-01 00:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO HIP_CTY_TASKS (id, name, description, status, job_id, device_id, device_operation, task_hour, task_minute, execution_days, execute_until)
VALUES (103, 'Task 103', 'Task 103 Description', 'ACT', 'defg', '123', 'DEACTIVATE', 23, 0, 'THU,FRI', '2024-01-01 00:00:00')
ON CONFLICT (id) DO NOTHING;

INSERT INTO HIP_CTY_TASKS (id, name, description, status, job_id, device_id, device_operation, task_hour, task_minute, execution_days, execute_until)
VALUES (104, 'Task 104', 'Task 104 Description', 'ACT', 'hijk', '456', 'DEACTIVATE', 21, 15, 'TUE,WED', '2027-01-01 00:00:00')
ON CONFLICT (id) DO NOTHING;
