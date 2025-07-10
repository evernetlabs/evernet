import datetime

from peewee import *
from .database import db


class EntityPropertySchema(Model):
    node_identifier = CharField()
    entity_schema_identifier = CharField()
    entity_schema_version = CharField()
    identifier = CharField()
    display_name = CharField()
    description = TextField()
    json_schema = TextField()
    creator = CharField()
    created_at = DateTimeField(default=datetime.datetime.now(tz=datetime.timezone.utc))
    updated_at = DateTimeField(default=datetime.datetime.now(tz=datetime.timezone.utc))

    class Meta:
        database = db
        indexes = (
            (('node_identifier', 'entity_schema_identifier', 'entity_schema_version', 'identifier'), True),
        )
