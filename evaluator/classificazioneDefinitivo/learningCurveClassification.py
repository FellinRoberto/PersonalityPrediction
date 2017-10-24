import matplotlib.pyplot as plt
#bow
plt.plot([100, 500, 1000, 2000, 5000, 10000, 15000], [0.9562, 0.9562, 0.9562, 0.9562, 0.9562, 0.9562, 0.9562], 'ro-', label="Openness")
plt.plot([100, 500, 1000, 2000, 5000, 10000, 15000], [0.8812, 0.8812, 0.8812, 0.8812, 0.8812, 0.8812, 0.8812], 'bo-', label="Conscientiousness")
plt.plot([100, 500, 1000, 2000, 5000, 10000, 15000], [0.8662, 0.8662, 0.8662, 0.8662, 0.8662, 0.8662, 0.8662], 'yo-', label="Extraversion")
plt.plot([100, 500, 1000, 2000, 5000, 10000, 15000], [0.9062, 0.9062, 0.9062, 0.9062, 0.9062, 0.9062, 0.9062], 'go-', label="Agreebleness")
plt.plot([100, 500, 1000, 2000, 5000, 10000, 15000], [0.5502, 0.5510, 0.5510, 0.5504, 0.5506, 0.5506, 0.5498], 'ko-', label="Neuroticism")

plt.title('Learning Curve Classification with threshold 2.5')
plt.legend(loc='upper right')
plt.ylabel('accuracy')
plt.xlabel('number of posts for training')
#plt.plot(x,y,'bo-',markevery=100)
plt.show()



