Task 1:

Fetching the employee to create reporting structure didn't fetched the other sub employees of employees under direct report,
so I added a method to initialize all the sub employees in order to create a proper tree structure in results.

Task 2:

created methods for create and read same as what the employee had in different controller, service and repo

compensation object wont store employee object because, if it does then, when the employee object is updated in database,
compensations object will also need to be updated.

Instead it only stores employee id, which will probably never change in db.

--- Added tests that checks the functions of the services

1. reporting structure
2. create and read for compensation


