import numpy as np
from numpy import recfromcsv

    
   
users = recfromcsv('../../personality/big5.sorted.csv', delimiter=',')

users_reduced = users[:1000]
print users_reduced
users_reduced.tofile('users_reduced.csv',sep=',')
np.savetxt('users_reduced.csv', users_reduced, delimiter=",", fmt="%s") 
#np.savetxt('users_reduced.csv', users_reduced, delimiter=',')


#last_id = users[1001,0]
#posts = recfromcsv('user_status.sorted.csv', delimiter=',')

#numpy.savetxt("users_reduced.csv", users_reduced, delimiter=",")

#import csv
#with open('big5.sorted.csv', 'rb') as csvfile:
#	users = csv.reader(csvfile)
#	for row in users:
#		print row

#import pandas as pd
#users = pd.read_csv('big5.sorted.csv')
#print(users.head(1000))
#np.array_split(df, 4)
#users_reduced = users [:,:]
#users.head(1000).to_csv('prova')

#target = users["Label"]  #provided your csv has header row, and the label column is named "Label"

#select all but the last column as data
#data = mydata.ix[:,:-1]
