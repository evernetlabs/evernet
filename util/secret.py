import random
import string


def generate_secret(n):
    characters = string.ascii_letters + string.digits
    random_string = ''.join(random.choices(characters, k=n))
    return random_string