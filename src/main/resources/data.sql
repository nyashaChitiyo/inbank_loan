
INSERT INTO segmentation (segment_id, segment_name, credit_modifier) values
('49002010965', 'debt',0),
('49002010976','segment 1',100),
('49002010987','segment 2',200),
('49002010998','segment 3',300);

INSERT INTO users (personal_code, username, segment_id) values
('1','john','49002010965'),
('2','peter','49002010976'),
('3','mary','49002010987'),
('4','sam','49002010998');