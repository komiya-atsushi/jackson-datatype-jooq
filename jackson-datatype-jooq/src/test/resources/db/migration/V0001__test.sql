CREATE TABLE test_table (
  int_col          INTEGER PRIMARY KEY,
  tinyint_col      TINYINT NOT NULL,
  bigint_col       BIGINT NOT NULL,
  decimal_col      DECIMAL(20, 2) NOT NULL,
  double_col       DOUBLE NOT NULL,
  string_col       VARCHAR (32) NOT NULL,
  bool_col         BOOLEAN NOT NULL,

  time_col         TIME NOT NULL,
  date_col         DATE NOT NULL,
  timestamp_tz_col TIMESTAMP WITH TIME ZONE NOT NULL,

  array_col        ARRAY NOT NULL,

  nullable_col     BOOLEAN
);

INSERT INTO test_table (int_col, tinyint_col, bigint_col, decimal_col, double_col, string_col, bool_col, time_col, date_col, timestamp_tz_col, array_col, nullable_col) VALUES
  (1, 2, 3, 4.5, 0.5, 'abc', true, '12:34:56', '2018-01-01', '2018-01-01 12:34:56+09:00', (), false),
  (2, 3, 4, 5.6, 0.25, 'ABC', false, '11:22:33', '2018-02-02', '2018-02-02 11:22:33Z', (1, 2), null),
  (3, 4, 5, 6.7, 0.125, '123', true, '22:33:44', '2018-03-03', '2018-03-03 22:33:44-09:00', (), null)
;