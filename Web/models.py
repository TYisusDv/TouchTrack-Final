from pymongo import MongoClient, ReturnDocument
from pymongo.errors import DuplicateKeyError
from config import *

db_mongo_client = MongoClient('mongodb://localhost:27017/')
db_mongo_main = db_mongo_client[config_app['db_mongo']['main']['name']]

def model_next_count(name):
    result = db_mongo_main.counters.find_one_and_update(
        {'_id': name},
        {'$inc': {'seq': 1}},
        upsert = True,
        return_document = ReturnDocument.AFTER
    )
    return result['seq']      

class model_users:
    @staticmethod
    def get(action = None, start = None, length = None, search = None, order_column = '_id', order_direction = 'asc', user_id = None, username = None):
        if action == 'one_user_id':
            data = db_mongo_main.users.find_one({'_id': user_id})
            return data
        elif action == 'one_username':
            data = db_mongo_main.users.find_one({'username': username})
            return data
        elif action == 'one':
            pipeline = [
                {'$lookup': {'from': 'user_roles', 'localField': 'user_role.$id', 'foreignField': '_id', 'as': 'user_role'}},                
                {'$unwind': {'path': '$user_role', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'user_persons', 'localField': 'user_person.$id', 'foreignField': '_id', 'as': 'user_person'}},                
                {'$unwind': {'path': '$user_person', 'preserveNullAndEmptyArrays': True}},
                {
                    '$match': {
                        '_id': user_id
                    }
                },
                {'$limit': 1}
            ]

            data = list(db_mongo_main.users.aggregate(pipeline))
            if not data:
                return None
            return data[0]
        elif action == 'all_table':
            pipeline = [
                {
                    '$match': {
                        '$or': [
                            {'_id': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'username': {'$regex': config_searchRegex(search), '$options': 'i'}}
                        ]
                    }
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$skip': start},
                {'$limit': length}
            ]

            data = list(db_mongo_main.users.aggregate(pipeline))
            return data
        elif action == 'all_table_count':
            pipeline = [
                {
                    '$match': {
                        '$or': [
                            {'_id': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'username': {'$regex': config_searchRegex(search), '$options': 'i'}}
                        ]
                    }
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$count': 'total'}
            ]

            data = list(db_mongo_main.users.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count        
        elif action == 'count_all':
            pipeline = [
                {'$count': 'total'}
            ]

            data = list(db_mongo_main.users.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count        

        return None
    
    @staticmethod
    def insert(action = None, user_id = None, username = None, password = 'x'):
        try: 
            if action == 'one_register':
                document = {
                    '_id': user_id,                    
                    'username': username,
                    'password': password,
                    'access': [],
                    'regdate': datetime.now(timezone.utc),
                } 

                db_mongo_main.users.insert_one(document, bypass_document_validation = True)
                
                return True
            return False
        except Exception as e:
            return False
    
    @staticmethod
    def update(action = None, user_id = None, username = None, password = 'x'):
        try:
            if action == 'one':
                document = {'_id': user_id} 

                update = {
                    '$set': {
                        'username': username,
                        'password': password,
                    }
                }

                db_mongo_main.users.update_one(document, update)
                return True
            
            return False
        except Exception as e:
            return False
        
class model_user_sessions: 
    @staticmethod
    def get(action = None, session_id = None):
        if action == 'session_id':
            data = db_mongo_main.user_sessions.find_one({'_id': session_id})
            return data
        
        return None  

    @staticmethod
    def insert(action = None, session_id = None, useragent = None, user_id = None):
        try: 
            if action == 'user':
                document = {
                    '_id': session_id,
                    'online': True,
                    'twofacauth': True,
                    'useragent': useragent,
                    'regdate': datetime.now(timezone.utc),
                    'user': {'$ref': 'users', '$id': user_id}
                }

                db_mongo_main.user_sessions.insert_one(document, bypass_document_validation = True)
                
                return True
            
            return False
        except Exception as e:
            return False

    @staticmethod
    def delete(action = None, session_id = None):
        try: 
            if action == 'session':
                document = {
                    '_id': session_id
                } 

                db_mongo_main.user_sessions.delete_many(document)
                
                return True
            
            return False
        except Exception as e:
            return False

class model_students:
    @staticmethod
    def get(action = None, start = None, length = None, search = None, order_column = '_id', order_direction = 'asc', student_id = None, career_id = None, semester_id = None, group_id = None, show = None, subquery = None):
        if action == 'one_student_id':
            pipeline = [
                {'$lookup': {'from': 'careers', 'localField': 'career.$id', 'foreignField': '_id', 'as': 'career'}},                
                {'$unwind': {'path': '$career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'groups', 'localField': 'group.$id', 'foreignField': '_id', 'as': 'group'}},                
                {'$unwind': {'path': '$group', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'semester.$id', 'foreignField': '_id', 'as': 'semester'}},                
                {'$unwind': {'path': '$semester', 'preserveNullAndEmptyArrays': True}},
                {
                    '$match': {
                        '_id': int(student_id) if student_id and student_id.isnumeric() else None
                    }
                },
                {'$limit': 1}
            ]

            data = list(db_mongo_main.students.aggregate(pipeline))
            if not data:
                return None
            
            return data[0]
        elif action == 'all_table':
            pipeline = [
                {'$lookup': {'from': 'careers', 'localField': 'career.$id', 'foreignField': '_id', 'as': 'career'}},                
                {'$unwind': {'path': '$career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'groups', 'localField': 'group.$id', 'foreignField': '_id', 'as': 'group'}},                
                {'$unwind': {'path': '$group', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'semester.$id', 'foreignField': '_id', 'as': 'semester'}},                
                {'$unwind': {'path': '$semester', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'fingerprints', 'localField': '_id', 'foreignField': 'student.$id', 'as': 'fingerprints'}},        
                {
                    '$project': {
                        '_id': 1,
                        'fullname': 1,
                        'career': 1,
                        'group': 1,
                        'semester': 1,                        
                        'fingerprints._id': 1                     
                    }
                },        
                {
                    '$match': {
                        '$or': [
                            {'_id': int(search) if search and search.isnumeric() else None},
                            {'fullname': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'career.name': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'group.name': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'semester._id': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'semester.name': {'$regex': config_searchRegex(search), '$options': 'i'}}
                        ]
                    }
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$skip': start},
                {'$limit': length}
            ]
            
            if career_id and career_id != 'all':
                pipeline[8]['$match'].update({'career._id': career_id})
            
            if semester_id and semester_id != 'all':
                pipeline[8]['$match'].update({'semester._id': semester_id})

            if group_id and group_id != 'all':
                pipeline[8]['$match'].update({'group._id': group_id})
            
            if show == 'no_fingerprints':
                pipeline[8]['$match'].update({'fingerprints': {'$size': 0}})

            data = list(db_mongo_main.students.aggregate(pipeline))
            return data        
        elif action == 'all_table_count':
            pipeline = [
                {'$lookup': {'from': 'careers', 'localField': 'career.$id', 'foreignField': '_id', 'as': 'career'}},                
                {'$unwind': {'path': '$career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'groups', 'localField': 'group.$id', 'foreignField': '_id', 'as': 'group'}},                
                {'$unwind': {'path': '$group', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'semester.$id', 'foreignField': '_id', 'as': 'semester'}},                
                {'$unwind': {'path': '$semester', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'fingerprints', 'localField': '_id', 'foreignField': 'student.$id', 'as': 'fingerprints'}},        
                {
                    '$project': {
                        '_id': 1,
                        'fullname': 1,
                        'career': 1,
                        'group': 1,
                        'semester': 1,                        
                        'fingerprints._id': 1                     
                    }
                },        
                {
                    '$match': {
                        '$or': [
                            {'_id': int(search) if search and search.isnumeric() else None},
                            {'fullname': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'career.name': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'group.name': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'semester._id': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'semester.name': {'$regex': config_searchRegex(search), '$options': 'i'}}
                        ]
                    }
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$count': 'total'}
            ]

            if career_id and career_id != 'all':
                pipeline[8]['$match'].update({'career._id': career_id})
            
            if semester_id and semester_id != 'all':
                pipeline[8]['$match'].update({'semester._id': semester_id})

            if group_id and group_id != 'all':
                pipeline[8]['$match'].update({'group._id': group_id})
            
            if show == 'no_fingerprints':
                pipeline[8]['$match'].update({'fingerprints': {'$size': 0}})

            data = list(db_mongo_main.students.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count        
        elif action == 'all_table_attendances':
            pipeline = [
                {'$lookup': {'from': 'careers', 'localField': 'career.$id', 'foreignField': '_id', 'as': 'career'}},                
                {'$unwind': {'path': '$career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'groups', 'localField': 'group.$id', 'foreignField': '_id', 'as': 'group'}},                
                {'$unwind': {'path': '$group', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'semester.$id', 'foreignField': '_id', 'as': 'semester'}},                
                {'$unwind': {'path': '$semester', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'attendances', 'localField': '_id', 'foreignField': 'student.$id', 'as': 'attendances'}},        
                {
                    '$project': {
                        '_id': 1,
                        'img': 1,
                        'fullname': 1,
                        'career': 1,
                        'group': 1,
                        'semester': 1,
                        'attendances.entry': 1,         
                        'attendances.regdate': 1,                        
                        'attendances.event.$id': 1                     
                    }
                },        
                {
                    '$match': {
                        '$or': subquery
                    }
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$skip': start},
                {'$limit': length}
            ] 

            data = list(db_mongo_main.students.aggregate(pipeline))
            return data  
        elif action == 'all_table_attendances_individual':
            pipeline = [
                {'$lookup': {'from': 'careers', 'localField': 'career.$id', 'foreignField': '_id', 'as': 'career'}},                
                {'$unwind': {'path': '$career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'groups', 'localField': 'group.$id', 'foreignField': '_id', 'as': 'group'}},                
                {'$unwind': {'path': '$group', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'semester.$id', 'foreignField': '_id', 'as': 'semester'}},                
                {'$unwind': {'path': '$semester', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'attendances_individual', 'localField': '_id', 'foreignField': 'student.$id', 'as': 'attendances_individual'}},        
                {
                    '$project': {
                        '_id': 1,
                        'img': 1,
                        'fullname': 1,
                        'career': 1,
                        'group': 1,
                        'semester': 1,
                        'attendances_individual.regdate': 1,                        
                    }
                },        
                {
                    '$match': {
                        '$or': subquery
                    }
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$skip': start},
                {'$limit': length}
            ] 

            data = list(db_mongo_main.students.aggregate(pipeline))
            return data        
        elif action == 'all_table_attendances_count':
            pipeline = [
                {'$lookup': {'from': 'careers', 'localField': 'career.$id', 'foreignField': '_id', 'as': 'career'}},                
                {'$unwind': {'path': '$career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'groups', 'localField': 'group.$id', 'foreignField': '_id', 'as': 'group'}},                
                {'$unwind': {'path': '$group', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'semester.$id', 'foreignField': '_id', 'as': 'semester'}},                
                {'$unwind': {'path': '$semester', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'attendances', 'localField': '_id', 'foreignField': 'student.$id', 'as': 'attendances'}},        
                {
                    '$project': {
                        '_id': 1,
                        'img': 1,
                        'fullname': 1,
                        'career': 1,
                        'group': 1,
                        'semester': 1,
                        'attendances.entry': 1,                        
                        'attendances.event.$id': 1                     
                    }
                },        
                {
                    '$match': {
                        '$or': subquery
                    }
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$count': 'total'}
            ] 

            data = list(db_mongo_main.students.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count 
        elif action == 'count_all':
            pipeline = [
                {'$count': 'total'}
            ]

            data = list(db_mongo_main.students.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count        

        return None

    @staticmethod
    def update(action = None, student_id = None, img = None, fullname = None, career_id = None, group_id = None, semester_id = None):
        try:
            if action == 'one':
                document = {'_id': int(student_id) if student_id and student_id.isnumeric() else None} 

                update = {
                    '$set': {
                        'img': img,
                        'fullname': fullname,
                        'career': {'$ref': 'careers', '$id': career_id},
                        'group': {'$ref': 'groups', '$id': group_id},
                        'semester': {'$ref': 'semesters', '$id': int(semester_id)}
                    }
                }

                db_mongo_main.students.update_one(document, update)
                return True
            
            return False
        except Exception as e:
            return False
    
    @staticmethod
    def insert(action = None, student_id = None, img = None, fullname = None, career_id = None, group_id = None, semester_id = None):
        try: 
            if action == 'one':
                document = {
                    '_id': int(student_id),    
                    'img': img,                
                    'fullname': fullname,
                    'career': {'$ref': 'careers', '$id': career_id},
                    'group': {'$ref': 'groups', '$id': group_id},
                    'semester': {'$ref': 'semesters', '$id': int(semester_id)}
                } 

                db_mongo_main.students.insert_one(document, bypass_document_validation = True)
                
                return True
            return False
        except Exception as e:
            return False

class model_fingerprints:
    @staticmethod
    def get(action = None, start = None, length = None, search = None, order_column = '_id', order_direction = 'asc', fingerprint_id = None, student_id = None):
        if action == 'all_student_fingerprints':
            pipeline = [               
                {
                    '$match': {
                        'student.$id': int(student_id) if student_id and student_id.isnumeric() else None
                    }
                },
                {
                    '$unset': 'student'
                }
            ]

            data = list(db_mongo_main.fingerprints.aggregate(pipeline))            
            return data
        elif action == 'all_students_fingerprints':
            pipeline = [               
                {'$lookup': {'from': 'students', 'localField': 'student.$id', 'foreignField': '_id', 'as': 'student'}},                
                {'$unwind': {'path': '$student', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'careers', 'localField': 'student.career.$id', 'foreignField': '_id', 'as': 'student.career'}},                
                {'$unwind': {'path': '$student.career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'groups', 'localField': 'student.group.$id', 'foreignField': '_id', 'as': 'student.group'}},                
                {'$unwind': {'path': '$student.group', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'student.semester.$id', 'foreignField': '_id', 'as': 'student.semester'}},                
                {'$unwind': {'path': '$student.semester', 'preserveNullAndEmptyArrays': True}},
            ]

            data = list(db_mongo_main.fingerprints.aggregate(pipeline))            
            return data
        elif action == 'all_table_student_fingerprints':
            pipeline = [
                {
                    '$match': {
                        'student.$id': student_id,
                        '$or': [
                            {'_id': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'regdate': {'$regex': config_searchRegex(search), '$options': 'i'}}
                        ]
                    }
                },
                {
                    '$unset': 'student'
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$skip': start},
                {'$limit': length}
            ]

            data = list(db_mongo_main.fingerprints.aggregate(pipeline))
            return data
        elif action == 'all_table_student_fingerprints_count':
            pipeline = [
                {
                    '$match': {
                        'student.$id': student_id,
                        '$or': [
                            {'_id': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'regdate': {'$regex': config_searchRegex(search), '$options': 'i'}}
                        ]
                    }
                },
                {
                    '$unset': 'student'
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$count': 'total'}
            ]

            data = list(db_mongo_main.fingerprints.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count        
        elif action == 'one_student_count':
            pipeline = [
                {
                    '$match': {
                        'student.$id': student_id
                    }
                },
                {'$count': 'total'}
            ]

            data = list(db_mongo_main.fingerprints.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count        
        elif action == 'one_fingerprint':
            pipeline = [
                {
                    '$match': {
                        '_id': fingerprint_id
                    }
                },
                {'$limit': 1}
            ]

            data = list(db_mongo_main.fingerprints.aggregate(pipeline))
            if not data:
                return None
            
            return data[0]  

        return None
    
    @staticmethod
    def insert(action = None, fingerprint_id = None, fingerprint = None, student_id = None):
        try:
            if action == 'one':
                document = {
                    '_id': fingerprint_id,
                    'fingerprint': fingerprint,
                    'regdate': datetime.now(timezone.utc),
                    'student': {'$ref': 'students', '$id': int(student_id)}
                }

                db_mongo_main.fingerprints.insert_one(document, bypass_document_validation = True)
                
                return True
            
            return False
        except Exception as e:
            return False

    @staticmethod
    def delete(action = None, fingerprint_id = None):
        try:
            if action == 'one':
                document = {
                    '_id': fingerprint_id
                }

                db_mongo_main.fingerprints.delete_one(document)
                
                return True
            
            return False
        except Exception as e:
            return False

class model_attendances:
    @staticmethod
    def get(action = None, start = None, length = None, search = None, order_column = '_id', order_direction = 'asc', subquery = None, event_id = None, student_id = None, entry = None):
        if action == 'all_student':
            pipeline = [
                {
                    '$match': {
                        'event.$id': event_id,
                        'student.$id': student_id,
                        'entry': entry
                    }
                },
            ]

            data = list(db_mongo_main.attendances.aggregate(pipeline))
            return data       
        elif action == 'all_table_filter':
            pipeline = [
                {'$lookup': {'from': 'students', 'localField': 'student.$id', 'foreignField': '_id', 'as': 'student'}},                
                {'$unwind': {'path': '$student', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'careers', 'localField': 'student.career.$id', 'foreignField': '_id', 'as': 'student.career'}},                
                {'$unwind': {'path': '$student.career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'groups', 'localField': 'student.group.$id', 'foreignField': '_id', 'as': 'student.group'}},                
                {'$unwind': {'path': '$student.group', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'student.semester.$id', 'foreignField': '_id', 'as': 'student.semester'}},                
                {'$unwind': {'path': '$student.semester', 'preserveNullAndEmptyArrays': True}},
                {
                    '$match': {
                        '$or': subquery
                    }
                },  
                {
                    '$project': {
                        'event': 0,
                    }
                },
                {
                    '$group': {
                        '_id': '$student._id',
                        'firstDocument': {f'$first': '$$ROOT'}
                    }
                },
                {'$replaceRoot': {'newRoot': f'$firstDocument'}},
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$skip': start},
                {'$limit': length}
            ]

            data = list(db_mongo_main.attendances.aggregate(pipeline))
            return data       
        elif action == 'all_table_filter_count':
            pipeline = [
                {'$lookup': {'from': 'students', 'localField': 'student.$id', 'foreignField': '_id', 'as': 'student'}},                
                {'$unwind': {'path': '$student', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'careers', 'localField': 'student.career.$id', 'foreignField': '_id', 'as': 'student.career'}},                
                {'$unwind': {'path': '$student.career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'groups', 'localField': 'student.group.$id', 'foreignField': '_id', 'as': 'student.group'}},                
                {'$unwind': {'path': '$student.group', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'student.semester.$id', 'foreignField': '_id', 'as': 'student.semester'}},                
                {'$unwind': {'path': '$student.semester', 'preserveNullAndEmptyArrays': True}},
                {
                    '$match': {
                        '$or': subquery
                    }
                },  
                {
                    '$project': {
                        'event': 0,
                    }
                },
                {
                    '$group': {
                        '_id': '$student._id',
                        'firstDocument': {f'$first': '$$ROOT'}
                    }
                },
                {'$replaceRoot': {'newRoot': f'$firstDocument'}},
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$count': 'total'}
            ]

            data = list(db_mongo_main.attendances.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count  
        elif action == 'all_table':
            pipeline = [
                {'$lookup': {'from': 'students', 'localField': 'student.$id', 'foreignField': '_id', 'as': 'student'}},                
                {'$unwind': {'path': '$student', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'careers', 'localField': 'student.career.$id', 'foreignField': '_id', 'as': 'student.career'}},                
                {'$unwind': {'path': '$student.career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'groups', 'localField': 'student.group.$id', 'foreignField': '_id', 'as': 'student.group'}},                
                {'$unwind': {'path': '$student.group', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'student.semester.$id', 'foreignField': '_id', 'as': 'student.semester'}},                
                {'$unwind': {'path': '$student.semester', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'events', 'localField': 'event.$id', 'foreignField': '_id', 'as': 'event'}},                
                {'$unwind': {'path': '$event', 'preserveNullAndEmptyArrays': True}},                
                {
                    '$match': {
                        '$or': [
                            {'student._id': int(search) if search and search.isnumeric() else None},
                            {'student.fullname': {'$regex': config_searchRegex(search), '$options': 'i'}},
                        ]
                    }
                },
                {
                    '$project': {
                        'event.careers': 0,
                    }
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$skip': start},
                {'$limit': length}
            ]

            data = list(db_mongo_main.attendances.aggregate(pipeline))
            return data       
        elif action == 'all_table_count':
            pipeline = [
                {'$lookup': {'from': 'students', 'localField': 'student.$id', 'foreignField': '_id', 'as': 'student'}},                
                {'$unwind': {'path': '$student', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'careers', 'localField': 'student.career.$id', 'foreignField': '_id', 'as': 'student.career'}},                
                {'$unwind': {'path': '$student.career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'groups', 'localField': 'student.group.$id', 'foreignField': '_id', 'as': 'student.group'}},                
                {'$unwind': {'path': '$student.group', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'student.semester.$id', 'foreignField': '_id', 'as': 'student.semester'}},                
                {'$unwind': {'path': '$student.semester', 'preserveNullAndEmptyArrays': True}},
                {
                    '$match': {
                        '$or': [
                            {'student._id': int(search) if search and search.isnumeric() else None},
                            {'student.fullname': {'$regex': config_searchRegex(search), '$options': 'i'}},
                        ]
                    }
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$count': 'total'}
            ]

            data = list(db_mongo_main.attendances.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count  
        elif action == 'count_all':
            pipeline = [
                {'$count': 'total'}
            ]

            data = list(db_mongo_main.attendances.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count
        
    @staticmethod
    def insert(action = None, student_id = None, event_id = None, entry = False):
        try:
            if action == 'one':
                document = {
                    '_id': model_next_count('attendance_id'),
                    'entry': entry,
                    'regdate': datetime.now(timezone.utc),
                    'student': {'$ref': 'students', '$id': student_id},
                    'event': {'$ref': 'events', '$id': event_id}
                }

                db_mongo_main.attendances.insert_one(document, bypass_document_validation = True)
                
                return True
            
            return False
        except Exception as e:
            return False

class model_attendances_individual:
    @staticmethod
    def get(action = None, start = None, length = None, search = None, order_column = '_id', order_direction = 'asc', subquery = None, event_id = None, student_id = None, entry = None):
        if action == 'all_table':
            pipeline = [
                {'$lookup': {'from': 'students', 'localField': 'student.$id', 'foreignField': '_id', 'as': 'student'}},                
                {'$unwind': {'path': '$student', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'careers', 'localField': 'student.career.$id', 'foreignField': '_id', 'as': 'student.career'}},                
                {'$unwind': {'path': '$student.career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'groups', 'localField': 'student.group.$id', 'foreignField': '_id', 'as': 'student.group'}},                
                {'$unwind': {'path': '$student.group', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'student.semester.$id', 'foreignField': '_id', 'as': 'student.semester'}},                
                {'$unwind': {'path': '$student.semester', 'preserveNullAndEmptyArrays': True}},
                {
                    '$match': {
                        '$or': [
                            {'student._id': int(search) if search and search.isnumeric() else None},
                            {'student.fullname': {'$regex': config_searchRegex(search), '$options': 'i'}},
                        ]
                    }
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$skip': start},
                {'$limit': length}
            ]

            data = list(db_mongo_main.attendances_individual.aggregate(pipeline))
            return data       
        elif action == 'all_table_count':
            pipeline = [
                {'$lookup': {'from': 'students', 'localField': 'student.$id', 'foreignField': '_id', 'as': 'student'}},                
                {'$unwind': {'path': '$student', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'careers', 'localField': 'student.career.$id', 'foreignField': '_id', 'as': 'student.career'}},                
                {'$unwind': {'path': '$student.career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'groups', 'localField': 'student.group.$id', 'foreignField': '_id', 'as': 'student.group'}},                
                {'$unwind': {'path': '$student.group', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'student.semester.$id', 'foreignField': '_id', 'as': 'student.semester'}},                
                {'$unwind': {'path': '$student.semester', 'preserveNullAndEmptyArrays': True}},
                {
                    '$match': {
                        '$or': [
                            {'student._id': int(search) if search and search.isnumeric() else None},
                            {'student.fullname': {'$regex': config_searchRegex(search), '$options': 'i'}},
                        ]
                    }
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$count': 'total'}
            ]

            data = list(db_mongo_main.attendances_individual.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count  
        elif action == 'count_all':
            pipeline = [
                {'$count': 'total'}
            ]

            data = list(db_mongo_main.attendances_individual.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count

        return None
        
    @staticmethod
    def insert(action = None, student_id = None):
        try:
            if action == 'one':
                document = {
                    '_id': model_next_count('attendance_individual_id'),
                    'regdate': datetime.now(timezone.utc),
                    'student': {'$ref': 'students', '$id': student_id},
                }

                db_mongo_main.attendances_individual.insert_one(document, bypass_document_validation = True)
                
                return True
            
            return False
        except Exception as e:
            return False

class model_groups:
    @staticmethod
    def get(action = None, start = None, length = None, search = None, order_column = '_id', order_direction = 'asc', group_id = None):
        if action == 'one_group_id':
            pipeline = [
                {
                    '$match': {
                        '_id': group_id
                    }
                },
                {'$limit': 1}
            ]

            data = list(db_mongo_main.groups.aggregate(pipeline))
            if not data:
                return None
            
            return data[0]
        elif action == 'all':
            data = list(db_mongo_main.groups.find())
            return data
        elif action == 'all_table':
            pipeline = [
                {
                    '$match': {
                        '$or': [
                            {'_id': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'name': {'$regex': config_searchRegex(search), '$options': 'i'}},
                        ]
                    }
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$skip': start},
                {'$limit': length}
            ]

            data = list(db_mongo_main.groups.aggregate(pipeline))
            return data       
        elif action == 'all_table_count':
            pipeline = [
                {
                    '$match': {
                        '$or': [
                            {'_id': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'name': {'$regex': config_searchRegex(search), '$options': 'i'}},
                        ]
                    }
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$count': 'total'}
            ]

            data = list(db_mongo_main.groups.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count              
        elif action == 'count_all':
            pipeline = [
                {'$count': 'total'}
            ]

            data = list(db_mongo_main.groups.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count        

        return None

    @staticmethod
    def insert(action = None, group_id = None, name = None):
        try: 
            if action == 'one':
                document = {
                    '_id': group_id,                    
                    'name': name
                } 

                db_mongo_main.groups.insert_one(document, bypass_document_validation = True)
                
                return True
            return False
        except Exception as e:
            return False

    @staticmethod
    def update(action = None, group_id = None, name = None):
        try:
            if action == 'one':
                document = {'_id': group_id} 

                update = {
                    '$set': {
                        'name': name,
                    }
                }

                db_mongo_main.groups.update_one(document, update)
                return True
            
            return False
        except Exception as e:
            return False

class model_careers:
    @staticmethod
    def get(action = None, start = None, length = None, search = None, order_column = '_id', order_direction = 'asc', career_id = None):
        if action == 'one_career_id':
            pipeline = [
                {
                    '$match': {
                        '_id': career_id
                    }
                },
                {'$limit': 1}
            ]

            data = list(db_mongo_main.careers.aggregate(pipeline))
            if not data:
                return None
            
            return data[0]
        elif action == 'all':
            data = list(db_mongo_main.careers.find())
            return data
        elif action == 'all_table':
            pipeline = [
                {
                    '$match': {
                        '$or': [
                            {'_id': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'name': {'$regex': config_searchRegex(search), '$options': 'i'}},
                        ]
                    }
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$skip': start},
                {'$limit': length}
            ]

            data = list(db_mongo_main.careers.aggregate(pipeline))
            return data       
        elif action == 'all_table_count':
            pipeline = [
                {
                    '$match': {
                        '$or': [
                            {'_id': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'name': {'$regex': config_searchRegex(search), '$options': 'i'}}
                        ]
                    }
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$count': 'total'}
            ]

            data = list(db_mongo_main.careers.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count         
        elif action == 'count_all':
            pipeline = [
                {'$count': 'total'}
            ]

            data = list(db_mongo_main.careers.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count        

        return None

    @staticmethod
    def insert(action = None, career_id = None, name = None):
        try: 
            if action == 'one':
                document = {
                    '_id': career_id,                    
                    'name': name
                } 

                db_mongo_main.careers.insert_one(document, bypass_document_validation = True)
                
                return True
            return False
        except Exception as e:
            return False

    @staticmethod
    def update(action = None, career_id = None, name = None):
        try:
            if action == 'one':
                document = {'_id': career_id} 

                update = {
                    '$set': {
                        'name': name,
                    }
                }

                db_mongo_main.careers.update_one(document, update)
                return True
            
            return False
        except Exception as e:
            return False

class model_semesters:
    @staticmethod
    def get(action = None, start = None, length = None, search = None, order_column = '_id', order_direction = 'asc', semester_id = None):
        if action == 'one_semester_id':
            pipeline = [
                {
                    '$match': {
                        '_id': int(semester_id) if semester_id and str(semester_id).isnumeric() else None
                    }
                },
                {'$limit': 1}
            ]

            data = list(db_mongo_main.semesters.aggregate(pipeline))
            if not data:
                return None
            
            return data[0]
        elif action == 'all':
            data = list(db_mongo_main.semesters.find())
            return data        

        return None

class model_events:
    @staticmethod
    def get(action = None, start = None, length = None, search = None, order_column = '_id', order_direction = 'asc', event_id = None, career_id = None, semester_id = None):
        if action == 'one_event_id':
            pipeline = [
                {
                    '$unwind': '$careers'
                },
                {'$lookup': {'from': 'careers', 'localField': 'careers.career.$id', 'foreignField': '_id', 'as': 'careers.career'}},                
                {'$unwind': {'path': '$careers.career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'careers.semester.$id', 'foreignField': '_id', 'as': 'careers.semester'}},                
                {'$unwind': {'path': '$careers.semester', 'preserveNullAndEmptyArrays': True}},
                {
                    '$match': {
                        '_id': event_id
                    }
                },
                {
                    '$group': {
                        '_id': '$_id',
                        'name': {'$first': '$name'},
                        'date_start': {'$first': '$date_start'},
                        'date_final': {'$first': '$date_final'},
                        'careers': {
                            '$push': {
                                'career': '$careers.career',
                                'semester': '$careers.semester'
                            }
                        }
                    }
                },
                {'$limit': 1}
            ]

            data = list(db_mongo_main.events.aggregate(pipeline))
            if not data:
                return None
            
            return data[0]
        elif action == 'all':
            data = list(db_mongo_main.events.find())
            return data
        elif action == 'all_table':
            pipeline = [
                {
                    '$unwind': '$careers'
                },
                {'$lookup': {'from': 'careers', 'localField': 'careers.career.$id', 'foreignField': '_id', 'as': 'careers.career'}},                
                {'$unwind': {'path': '$careers.career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'careers.semester.$id', 'foreignField': '_id', 'as': 'careers.semester'}},                
                {'$unwind': {'path': '$careers.semester', 'preserveNullAndEmptyArrays': True}},
                {
                    '$match': {
                        '$or': [
                            {'_id': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'name': {'$regex': config_searchRegex(search), '$options': 'i'}},
                        ]
                    }
                },
                {
                    '$group': {
                        '_id': '$_id',
                        'name': {'$first': '$name'},
                        'date_start': {'$first': '$date_start'},
                        'date_final': {'$first': '$date_final'},
                        'careers': {
                            '$push': {
                                'career': '$careers.career',
                                'semester': '$careers.semester'
                            }
                        }
                    }
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$skip': start},
                {'$limit': length}
            ]

            data = list(db_mongo_main.events.aggregate(pipeline))
            return data       
        elif action == 'all_table_count':
            pipeline = [
                {
                    '$unwind': '$careers'
                },
                {'$lookup': {'from': 'careers', 'localField': 'careers.career.$id', 'foreignField': '_id', 'as': 'careers.career'}},                
                {'$unwind': {'path': '$careers.career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'careers.semester.$id', 'foreignField': '_id', 'as': 'careers.semester'}},                
                {'$unwind': {'path': '$careers.semester', 'preserveNullAndEmptyArrays': True}},
                {
                    '$match': {
                        '$or': [
                            {'_id': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            {'name': {'$regex': config_searchRegex(search), '$options': 'i'}},
                        ]
                    }
                },
                {
                    '$group': {
                        '_id': '$_id',
                        'name': {'$first': '$name'},
                        'date_start': {'$first': '$date_start'},
                        'date_final': {'$first': '$date_final'},
                        'careers': {
                            '$push': {
                                'career': '$careers.career',
                                'semester': '$careers.semester'
                            }
                        }
                    }
                },
                {'$sort': {order_column: 1 if order_direction == 'asc' else -1}},
                {'$count': 'total'}
            ]

            data = list(db_mongo_main.events.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count         
        elif action == 'all_filter_start':
            pipeline = [
                {
                    '$unwind': '$careers'
                },
                {'$lookup': {'from': 'careers', 'localField': 'careers.career.$id', 'foreignField': '_id', 'as': 'careers.career'}},                
                {'$unwind': {'path': '$careers.career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'careers.semester.$id', 'foreignField': '_id', 'as': 'careers.semester'}},                
                {'$unwind': {'path': '$careers.semester', 'preserveNullAndEmptyArrays': True}},
                {
                    '$match': {
                        'careers.career._id': career_id,
                        'careers.semester._id': semester_id,                        
                        'date_start': {
                            '$gte': datetime.now(timezone.utc) - timedelta(minutes=20),
                            '$lte': datetime.now(timezone.utc) + timedelta(minutes=10)
                        }                           
                    }
                },
                {
                    '$group': {
                        '_id': '$_id',
                        'name': {'$first': '$name'},
                        'date_start': {'$first': '$date_start'},
                        'date_final': {'$first': '$date_final'},
                        'careers': {
                            '$push': {
                                'career': '$careers.career',
                                'semester': '$careers.semester'
                            }
                        }
                    }
                }
            ]

            data = list(db_mongo_main.events.aggregate(pipeline))
            return data       
        elif action == 'all_filter_final':
            pipeline = [
                {
                    '$unwind': '$careers'
                },
                {'$lookup': {'from': 'careers', 'localField': 'careers.career.$id', 'foreignField': '_id', 'as': 'careers.career'}},                
                {'$unwind': {'path': '$careers.career', 'preserveNullAndEmptyArrays': True}},
                {'$lookup': {'from': 'semesters', 'localField': 'careers.semester.$id', 'foreignField': '_id', 'as': 'careers.semester'}},                
                {'$unwind': {'path': '$careers.semester', 'preserveNullAndEmptyArrays': True}},
                {
                    '$match': {
                        'careers.career._id': career_id,
                        'careers.semester._id': semester_id,                        
                        'date_final': {
                            '$gte': datetime.now(timezone.utc) - timedelta(minutes=10),
                            '$lte': datetime.now(timezone.utc) + timedelta(minutes=20)
                        }                           
                    }
                },
                {
                    '$group': {
                        '_id': '$_id',
                        'name': {'$first': '$name'},
                        'date_start': {'$first': '$date_start'},
                        'date_final': {'$first': '$date_final'},
                        'careers': {
                            '$push': {
                                'career': '$careers.career',
                                'semester': '$careers.semester'
                            }
                        }
                    }
                }
            ]

            data = list(db_mongo_main.events.aggregate(pipeline))
            return data       
        elif action == 'count_all':
            pipeline = [
                {'$count': 'total'}
            ]

            data = list(db_mongo_main.events.aggregate(pipeline))          
            count = data[0]['total'] if data else 0 
            return count        

        return None

    @staticmethod
    def insert(action = None, name = None, date_start = None, date_final = None, careers = None):
        try: 
            if action == 'one':
                document = {
                    '_id': model_next_count('event_id'),                    
                    'name': name,
                    'date_start': date_start,
                    'date_final': date_final,
                    'careers': careers,                   
                } 

                db_mongo_main.events.insert_one(document, bypass_document_validation = True)
                
                return True
            return False
        except Exception as e:
            return False

    @staticmethod
    def update(action = None, event_id = None, name = None, date_start = None, date_final = None, careers = None):
        try:
            if action == 'one':
                document = {'_id': event_id} 

                update = {
                    '$set': {
                        'name': name,
                        'date_start': date_start,
                        'date_final': date_final,
                        'careers': careers,  
                    }
                }

                db_mongo_main.events.update_one(document, update)
                return True
            
            return False
        except Exception as e:
            return False
