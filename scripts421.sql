UPDATE student SET age = 16 WHERE age < 16;

WITH duplicates AS (
    SELECT id, row_number() OVER (PARTITION BY name ORDER BY id) AS rn
    FROM student
)
UPDATE student s
SET name = s.name || '_' || s.id
FROM duplicates d
WHERE s.id = d.id
  AND d.rn > 1;

ALTER TABLE student ALTER COLUMN name SET NOT NULL;
ALTER TABLE student ADD CONSTRAINT student_name_unique UNIQUE (name);

ALTER TABLE student ADD CONSTRAINT student_age_check CHECK (age >= 16);

ALTER TABLE student ALTER COLUMN age SET DEFAULT 20;

ALTER TABLE faculty ADD CONSTRAINT faculty_name_color_unique UNIQUE (name, color);
