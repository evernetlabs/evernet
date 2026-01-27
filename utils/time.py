import datetime


def current_datetime():
    return datetime.datetime.now(tz=datetime.timezone.utc)
