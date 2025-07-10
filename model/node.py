import datetime

from peewee import *
from .database import db


class Node(Model):
    identifier = CharField(unique=True)
    display_name = CharField()
    description = TextField()
    signing_private_key = CharField()
    signing_public_key = CharField()
    open = BooleanField(default=True)
    creator = CharField()
    created_at = DateTimeField(default=datetime.datetime.now(tz=datetime.timezone.utc))
    updated_at = DateTimeField(default=datetime.datetime.now(tz=datetime.timezone.utc))

    class Meta:
        database = db
