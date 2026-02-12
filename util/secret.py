import secrets
import string


def generate_secret(n: int) -> str:
    if n <= 0:
        raise ValueError("Length must be a positive integer")

    alphabet = string.ascii_letters + string.digits
    return ''.join(secrets.choice(alphabet) for _ in range(n))
