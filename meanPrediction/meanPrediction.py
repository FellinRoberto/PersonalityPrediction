import csv
from sklearn.metrics import mean_squared_error

sum=[0,0,0,0,0]
c=0
<<<<<<< HEAD

#read the input and do the sum of the parameter of all the user, output array with size 5 with the sum of the parameter
with open('../../personality/big5.sorted2.csv') as fin:
=======
with open('../dataset/users_reduced.csv') as fin:
>>>>>>> 05b5b8bdd984782c369ab3a68995b2db3ece2acd
    headerline = fin.next()
    total = 0

    for row in csv.reader(fin):
        c+=1
        for x in range(1, 6):
            print row[x]
            sum[x-1] = round(sum[x-1] + float(row[x]),2)


# do the mean, dividing the sum for the number of users
sum=[x/c for x in sum]
print sum

#read the parameter and use it as input
y_true = [[0.5, 1, 0.5, 1, 1],[-1, 1, 0.5, 1, 1],[7, -6, 0.5, 1, 1]]

#compute the prediction as the mean previous calculated
y_pred=[]

for x in range(1, 4):
    y_pred.append (sum)

print y_true
print y_pred
print mean_squared_error(y_true, y_pred)
