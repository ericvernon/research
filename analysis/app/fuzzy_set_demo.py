import numpy as np
import matplotlib.pyplot as plt
from lib.fuzzy_membership import fuzzy_membership

x = np.linspace(0, 1, 1001)
y = [np.vectorize(fuzzy_membership)(set_id, x) for set_id in range(30)]

# Show 'small', 'medium', and 'large'
fig, ax = plt.subplots(1, 3, figsize=(12, 4), sharey=True)

ax[0].set_xlabel('Small', fontsize=14)
ax[0].plot(x, y[3], '--k')

ax[1].set_xlabel('Medium', fontsize=14)
ax[1].plot(x, y[4], '--k')

ax[2].set_xlabel('Large', fontsize=14)
ax[2].plot(x, y[5], '--k')

plt.setp(ax, xlim=(0, 1), ylim=(0, 1))
plt.show()
