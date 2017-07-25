import csv

sum=[0,0,0,0,0]
c=0
with open('../dataset/users_reduced.csv') as fin:
    headerline = fin.next()
    total = 0

    for row in csv.reader(fin):
        c+=1
        for x in range(1, 6):
            print row[x]
            sum[x-1] = round(sum[x-1] + float(row[x]),2)


sum=[x/c for x in sum]
print sum
