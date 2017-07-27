import csv
import pandas as pd

sum=[0,0,0,0,0]
c=0

#read the input and do the sum of the parameter of all the user, output array with size 5 with the sum of the parameter
with open('../dataset/y_train.csv') as fin:

    headerline = fin.next()
    total = 0

    for row in csv.reader(fin):
        c+=1
        for x in range(0, 5):
            sum[x-1] = round(sum[x-1] + float(row[x]),2)


# do the mean, dividing the sum for the number of users
sum=[x/c for x in sum]
print "prediction: ",sum

#compute the prediction as the mean previous calculated
y_pred=[]
users=pd.read_csv('../dataset/X_test.csv', engine='c')

for x in range(1, len(users)):
    y_pred.append (sum)

y_pred=pd.DataFrame(y_pred)
y_pred.to_csv('y_pred.csv', index=False, quoting=csv.QUOTE_NONNUMERIC, doublequote=True)