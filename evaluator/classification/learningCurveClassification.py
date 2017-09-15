import matplotlib.pyplot as plt
#bow
plt.plot([500, 1000, 2000, 4000], [0.6965, 0.696, 0.696, 0.697], 'ro-', label="Openness")
plt.plot([500, 1000, 2000, 4000,], [0.554, 0.5505, 0.5415, 0.5495], 'bo-', label="Conscientiousness")
plt.plot([500, 1000, 2000, 4000], [0.5425, 0.539, 0.5410, 0.538], 'yo-', label="Extraversion")
plt.plot([500, 1000, 2000, 4000,], [0.4965, 0.5, 0.501, 0.511], 'go-', label="Agreebleness")
plt.plot([500, 1000, 2000, 4000], [0.814, 0.814, 0.814, 0.814], 'ko-', label="Neuroticism")

plt.title('Learning Curve Classification with threshold 3.5')
plt.legend(loc='upper right')
plt.ylabel('accuracy')
plt.xlabel('number of posts for training')
#plt.plot(x,y,'bo-',markevery=100)
plt.show()



