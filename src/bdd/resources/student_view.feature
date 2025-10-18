Feature: StudentApplicationFrame

Scenario: The initial state of the view
  Given The database contains the students with the following values
    | 1 | firststudent |
    | 2 | secondstudent |
  When The Student View is shown
  Then The list contains elements with the following values
    | 1 | firststudent |
    | 2 | secondstudent |
