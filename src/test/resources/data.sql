INSERT INTO types (id, value) VALUES
  (1, 'Type 1'),
  (2, 'Type 2');

INSERT INTO groups (id, name, parent_id, type_id, owner) VALUES
  (1, 'Test 1', 4, 1, 'test'),
  (2, 'Test 2', NULL, 1, 'test'),
  (3, 'Test Child of 1', 1, 1, 'test'),
  (4, 'Test Parent of 1', NULL, 1, 'test'),
  (5, 'Test 3', null, 2, 'test'),
  (6, 'Test 4', null, 2, 'test'),
  (7, 'Test 5', 6, 2, 'test'),
  (8, 'Test 6', 6, 2, 'test');

INSERT INTO statuses (id, value) VALUES
  (1, 'pending');

INSERT INTO samples (id, status_id, type_id, owner) VALUES
  (1, 1, 1, 'test'),
  (2, 1, 1, 'test'),
  (3, 1, 1, 'test'),
  (4, 1, 1, 'test'),
  (5, 1, 2, 'test'),
  (6, 1, 2, 'test');

INSERT INTO groups_samples (group_id, sample_id) VALUES
  (1, 1),
  (1, 2),
  (1, 3),
  (2, 4),
  (2, 5);
