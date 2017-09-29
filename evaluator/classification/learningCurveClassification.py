import matplotlib.pyplot as plt
#bow
plt.plot([100, 500, 2000, 5000, 10000], [0.7077, 0.7071, 0.7082, 0.708, 0.7061], 'ro-', label="Openness")
plt.plot([100, 500, 2000, 5000, 10000], [0.5036, 0.5242, 0.5403, 0.5037, 0.5452], 'bo-', label="Conscientiousness")
plt.plot([100, 500, 2000, 5000, 10000], [0.5214, 0.5114, 0.5159, 0.5222, 0.5191], 'yo-', label="Extraversion")
plt.plot([100, 500, 2000, 5000, 10000], [0.4546, 0.5038, 0.5269, 0.5215, 0.5149], 'go-', label="Agreebleness")
plt.plot([100, 500, 2000, 5000, 10000], [0.8319, 0.8319, 0.8319, 0.8319, 0.8319], 'ko-', label="Neuroticism")

plt.title('Learning Curve Classification with threshold 3.5')
plt.legend(loc='upper right')
plt.ylabel('accuracy')
plt.xlabel('number of posts for training')
#plt.plot(x,y,'bo-',markevery=100)
plt.show()



