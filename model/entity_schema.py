import datetime

from peewee import *
from .database import db


class EntitySchema(Model):
    node_identifier = CharField()
    identifier = CharField()
    version = CharField()
    display_name = CharField()
    description = CharField()
    creator = CharField()
    created_at = DateTimeField(default=datetime.datetime.now(tz=datetime.timezone.utc))
    updated_at = DateTimeField(default=datetime.datetime.now(tz=datetime.timezone.utc))

    class Meta:
        database = db
        indexes = (
            (('node_identifier', 'identifier', 'version'), True),
        )
