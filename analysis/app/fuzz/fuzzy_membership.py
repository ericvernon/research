import numpy as np
import matplotlib.pyplot as plt

def fuzzy_membership(fuzzy_set_id, value):
  """
  Get the fuzzy membership of a value into a fuzzy set.  This assumes the
  standard triangle layout, where set 0 is "don't care", 1 and 2 are "big" and
  "small" respectively, etc.
  This uses the formula described in the literature.
  """
  if fuzzy_set_id == 0:
    return 1.0

  k = get_order_of_interval(fuzzy_set_id)
  K = get_number_of_intervals(fuzzy_set_id)
  a = (k - 1) / (K - 1)
  b = 1.0 / (K - 1)
  membership = 1.0 - ( (abs(a - value)) / b)
  return max(membership, 0.0)

def get_order_of_interval(fuzzy_set_id):
  """
  Given a fuzzy set number, get the 0-index of that fuzzy membership function
  within all membership functions that share that hyperspace.  For example, '4'
  represents 'medium' within the space shared by 'small', 'medium', and 'large',
  so the return value is 1.
  This is referred to by a lower-case k in the literature.
  """
  previous_sets = 0
  current_sets = 2
  while fuzzy_set_id > previous_sets + current_sets:
    previous_sets += current_sets
    current_sets += 1

  return fuzzy_set_id - previous_sets

def get_number_of_intervals(fuzzy_set_id):
  """
  Given a fuzzy set number, get the number of fuzzy membership functions which
  share that same hyperspace.  For example, '3', '4', and '5' represent 'small',
  'medium', and 'large' within the space they all share.  Therefore the return
  value for all three is 3.
  This is referred to by a capital K in the literature.
  """
  n_triangles = 1;
  total_checked = 0;
  while fuzzy_set_id > total_checked:
    n_triangles += 1
    total_checked += n_triangles;
  return n_triangles

def compatibility_2d(fuzzy_sets, x, y):
  set1, set2 = fuzzy_sets
  return fuzzy_membership(set1, x) * fuzzy_membership(set2, y)

def set_id_to_english(set_id):
  if set_id == 0:
    return "Don't Care"
  if set_id == 1:
    return "Small-ish"
  if set_id == 2:
    return "Large-ish"
  if set_id == 3:
    return "Fairly Small"
  if set_id == 4:
    return "Fairly Medium"
  if set_id == 5:
    return "Fairly Large"
  if set_id == 6:
    return "Small"
  if set_id == 7:
    return "Medium-Small"
  if set_id == 8:
    return "Medium-Large"
  if set_id == 9:
    return "Large"
  if set_id == 10:
    return "Very Small"
  if set_id == 11:
    return "Medium-Small"
  if set_id == 12:
    return "Very Medium"
  if set_id == 13:
    return "Medium-Large"
  if set_id == 14:
    return "Very Large"
