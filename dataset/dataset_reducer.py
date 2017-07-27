import pandas as pd
import csv
import numpy as np

#read complete user list
users=pd.read_csv('../../personality/big5.sorted.csv',escapechar='/',engine='c')

reduced_size = 1000

#compute and save reduced user list
users_reduced = users[:reduced_size]
users_reduced.to_csv('users_1000.csv', index=False, quoting=csv.QUOTE_NONNUMERIC, doublequote=True)

##check if the csv was saved correctly
#df2=pd.read_csv('../../personality/users_1000.csv',engine='c')
#print users_reduced.equals(df2)

#take first userID not in the reduced list
last_id = users.get_value(reduced_size, 'userid')

posts = pd.read_csv('../../personality/xaa.csv',escapechar='/',engine='c')
i=0
while i<len(posts) and posts.get_value(i, 'userid')!=last_id:	
    i=i+1
    
#i is now the index of the first post that does not belong to one of the users in the reduced list
print i

posts_reduced = posts[:i]
posts_reduced.to_csv('posts_1000.csv', index=False, quoting=csv.QUOTE_NONNUMERIC, doublequote=True)

##check if the csv was saved correctly
#df3=pd.read_csv('../../personality/posts_1000.csv',engine='c')
#print  posts_reduced.equals(df3)



#print posts.get_value(i-1, 'userid')
#print posts.get_value(i, 'userid')

#print posts.get_value(118313, 'status_update')
#np.where(posts['status_update']=='linis ng room kc ngaun lng ako nag ka ilaw ulit sa room....:D')
