import time

TABLE_SIZE = 64

HASHING = 7


HEADER = '\033[95m'
OKBLUE = '\033[94m'
OKGREEN = '\033[92m'
WARNING = '\033[93m'
FAIL = '\033[91m'
ENDC = '\033[0m'


def the_print(info):
    print "[CACHE]", info



table = [[] for i in range(TABLE_SIZE)]


def insert(incident_list, from_value, to_value):
    if to_value is None:
        to_value = 0
    the_print("add data to cache table")
    new_item = {
        "incidents": incident_list,
        "from": from_value,
        "to": to_value,
        "time": time.time()
    }
    slot_num = ((from_value + to_value) * HASHING) % TABLE_SIZE
    slot = table[slot_num]
    the_print("slot: " + str(slot_num) + " size: " + str(len(slot)))
    found = False
    for index, item in enumerate(slot):
        if item['from'] == from_value and item['to'] == to_value:
            slot[index] = new_item
            found = True
            break
    if not found:
        slot.append(new_item)


def get(from_value, to_value):
    the_print("search cache table")
    if to_value is None:
        to_value = 0
    slot_num = ((from_value + to_value) * HASHING) % TABLE_SIZE
    slot = table[slot_num]
    the_print("slot: " + str(slot_num) + " size: " + str(len(slot)))
    for index, item in enumerate(slot):
        if item['from'] == from_value and item['to'] == to_value:
            if time.time() - item['time'] <= 30:
                the_print("found in cache table")
                return item['incidents']
        if time.time() - item['time'] > 30:
            slot.remove(item)
            the_print("data in cache is out of date")
    the_print("not found in cache table")
    return None