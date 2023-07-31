# inbank_loan
There are 4 hard corded user in the H2 data base

user with id 1 is in debt
user with id 2 is in segment 1 category 
user with id 3 is in segment 2 category
user with id 4 is in segment 3 category

To make api calls use post request

http://localhost:8082/loan { "personalCode": "3", "loanAmount": 5000, "loanPeriod": 12 }

the response if approved is { "result": true, "maxApprovedAmount": 2400, "approvedPeriod": 12 }
