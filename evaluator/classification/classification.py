result=""
for i in range(1,6):
    with open('../../minimalpipeline-master/TreeKernel/train'+str(i)+'.dat') as fp:
        for line in fp:
            x = line.split(" ")[0]

            x= float(x)

            if x > 3.5:
                v = 1
            else:
                v = -1

            s = line.replace(str(x) + " ", str(v)+" ")

            result=result+s

    out_file = open("train"+str(i)+"C.dat","w")
    out_file.write(result)
    out_file.close()
    result = ""


result=""
for i in range(1,6):
    with open('../../minimalpipeline-master/TreeKernel/test'+str(i)+'.dat') as fp:
        for line in fp:
            x = line.split(" ")[0]

            x= float(x)

            if x > 3.5:
                v = 1
            else:
                v = -1

            s = line.replace(str(x) + " ", str(v)+" ")

            result=result+s

    out_file = open("test"+str(i)+"C.dat","w")
    out_file.write(result)
    out_file.close()
    result = ""