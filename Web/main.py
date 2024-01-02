from config import *
from models import *

app = Flask(__name__, static_url_path='/assets', static_folder='static')
app.secret_key = 'Secret_TouchTrack'

def main_sessionVerify():
    #0 = No Logged
    #1 = Logged
    session_id = session.get('session_id')
    user_id = session.get('user_id')

    if not session_id:
        return 0    
    elif not user_id:
        return 0
    
    session_verify = model_user_sessions.get(action = "session_id", session_id = session_id)
    if not session_verify:
        session.clear()
        return 0
    elif user_id != session_verify["user"].id:
        session.clear()
        return 0
    
    return 1

@app.route('/api/web/data/download/<name>', methods=['GET'])
def api_web_data_download(name):
    path_folder = 'download'
    extensiones_custom = {
        '.pdf': 'application/pdf',
        '.xlsx': 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet',
        '.jpg': 'image/jpeg',
        '.txt': 'text/plain',
    }

    for ext, mime_type in extensiones_custom.items():
        if os.path.exists(os.path.join(path_folder, name + ext)):
            return send_from_directory(path_folder, name + ext, as_attachment=True, mimetype=mime_type)
    
    return render_template('/error.html', code = '404', msg = 'Página no encontrada.'), 404

@app.route('/', defaults={'path': ''})
@app.route('/<path:path>', methods=['GET', 'POST', 'PUT', 'DELETE', 'PATCH', 'OPTIONS'])
def main_web(path):    
    try:
        v_config_splitList = [config_splitList('/', path, i) for i in range(10)]

        v_requestForm = request.form
        v_requestArgs = request.args 
        v_sessionVerify =  main_sessionVerify()
        v_session_id = session.get('session_id')
        v_user_id = session.get('user_id')
        v_action = v_requestForm.get('action')
        v_action_param = v_requestArgs.get('action')
        datetime_utc = datetime.now(timezone.utc)
        datetime_now = datetime.now()
        
        v_requestJson = {}
        v_action_json = None
        try:
            v_requestJson = request.get_json()
        except:
            pass

        v_action_json = v_requestJson.get('action')

        if request.method == 'GET' and path in config_routes:
            if v_sessionVerify == 0:
                return render_template('/auth/index.html')

            user_info = model_users.get(action = 'one_user_id', user_id = v_user_id)
            return render_template('/index.html', user_info = user_info)
        
        elif request.method == 'GET' and path == 'auth/logout':
            if v_sessionVerify == 1:
                model_user_sessions.delete(action = 'session', session_id = v_session_id)

            session.clear()
            return redirect('/')
        
        if v_sessionVerify == 0:
            if request.method == 'POST' and path == 'api/web/data/auth':
                if v_action == 'login':  
                    username = v_requestForm.get('username')
                    if not config_validateForm(form = username, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un usuario válido e inténtelo de nuevo.'})
                    
                    password = v_requestForm.get('password')
                    if not config_validateForm(form = password, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione una contraseña válida e inténtelo de nuevo.'})

                    user_verify = model_users.get(action = 'one_username', username = username)
                    if not user_verify:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un correo o contraseña válidos e inténtelo de nuevo.'})
                    
                    if not bcrypt.verify(password, user_verify['password']):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un usuario o contraseña válidos e inténtelo de nuevo.'})
                    
                    session_id = str(uuid.uuid4())
                    user_agent = request.headers.get('User-Agent')

                    signin = model_user_sessions.insert(action = 'user', session_id = session_id, useragent = user_agent, user_id = user_verify['_id'])
                    if not signin:
                        return jsonify({'success': False, 'msg': 'No se pudo iniciar sesión. Por favor, inténtelo de nuevo. Si el problema persiste, no dude en ponerse en contacto con nosotros para obtener ayuda.'})
                    
                    session["user_id"] = user_verify['_id']
                    session["session_id"] = session_id
                    return jsonify({'success': True, 'msg': 'Inicio de sesión correcto. ¡Bienvenido/a! Redireccionando...'})
        
        elif v_sessionVerify == 1:
            #DASHBOARD
            if request.method == 'GET' and path == 'api/web/widget/dashboard':
                return jsonify({'success': True, 'html': render_template('/home/dashboard.html')})
            
            #TABLE
            elif request.method == 'POST' and path == 'api/web/data/table':
                start = v_requestForm.get('start')
                if not config_validateForm(form = start, min = 1):
                    start = 0
                
                length = v_requestForm.get('length')
                if not config_validateForm(form = length, min = 1):
                    length = 10
                
                search = v_requestForm.get('search')
                if not config_validateForm(form = search, min = 1):
                    search = ''

                order_column = v_requestForm.get('order_column')
                if not config_validateForm(form = order_column, min = 1):
                    order_column = '_id'

                order_direction = v_requestForm.get('order_direction')
                if not config_validateForm(form = order_direction, min = 1):
                    order_direction = 'asc'

                data = []
                data_count = 0 

                if v_action == 'manage_users':
                    data = model_users.get(action = 'all_table', start = int(start), length = int(length), search = search, order_column = order_column, order_direction = order_direction)
                    for item in data:
                        item['actions'] = f'<div class="table-actions"><a href="/manage/user/edit?id={item["_id"]}" class="btn-sm bg-outline-primary"><i class="fa-solid fa-pen-to-square"></i></a></div>'
                        item['regdate'] = f'<span class="badge bg-primary">{config_convertDate(item["regdate"])}</span>'
                        
                    data_count = model_users.get(action = 'all_table_count', search = search, order_column = order_column, order_direction = order_direction)
                elif v_action == 'manage_students':
                    career_id = v_requestForm.get('career_id')
                    semester_id = v_requestForm.get('semester_id') 
                    show = v_requestForm.get('show') 
                    group_id = v_requestForm.get('group_id') 
                    
                    data = model_students.get(action = 'all_table', start = int(start), length = int(length), search = search, order_column = order_column, order_direction = order_direction, career_id = career_id, semester_id = int(semester_id) if semester_id and str(semester_id).isnumeric() else None, group_id = group_id, show = show)
                    for item in data:
                        item['actions'] = (
                            '<div class="table-actions">'
                                f'<a href="/manage/student/edit?id={item["_id"]}" class="btn-sm bg-outline-primary">'
                                    '<i class="fa-solid fa-pen-to-square"></i>'
                                '</a>'
                                f'<a href="/manage/student/fingerprints?id={item["_id"]}" class="btn-sm bg-outline-warning">'
                                    '<i class="fa-solid fa-fingerprint"></i>'
                                '</a>'
                            '</div>'
                        )
                        item['_id'] = f'<span class="badge bg-primary">{item["_id"]}</span>'
                        item['career']['name'] = f'<span class="badge bg-primary">{item["career"]["name"]}</span>'
                        item['semester']['name'] = f'<span class="badge bg-primary">{item["semester"]["name"]}</span>'

                    data_count = model_students.get(action = 'all_table_count', search = search, order_column = order_column, order_direction = order_direction, career_id = career_id, semester_id = int(semester_id) if semester_id and str(semester_id).isnumeric() else None, group_id = group_id, show = show)
                elif v_action == 'manage_student_fingerprints':
                    student_id = v_requestForm.get('student_id')
                    if not config_validateForm(form = student_id, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un numero de control válido e inténtelo de nuevo.'})
                    
                    data = model_fingerprints.get(action = 'all_table_student_fingerprints', start = int(start), length = int(length), search = search, order_column = order_column, order_direction = order_direction, student_id = int(student_id) if student_id and str(student_id).isnumeric() else None)
                    for item in data:
                        item['actions'] = (
                            '<div class="table-actions">'
                                f'<a href="javascript:;" class="btn-sm bg-outline-danger delete-fingerprint" data-fingerprint-id="{item["_id"]}">'
                                    '<i class="fa-solid fa-trash"></i>'
                                '</a>'
                            '</div>'
                        )
                        item['_id'] = f'<span class="badge bg-primary">{item["_id"]}</span>'        
                        item['regdate'] = f'<span class="badge bg-primary">{config_convertDate(item["regdate"])}</span>'                

                    data_count = model_fingerprints.get(action = 'all_table_student_fingerprints_count', search = search, order_column = order_column, order_direction = order_direction, student_id = int(student_id) if student_id and str(student_id).isnumeric() else None)
                elif v_action == 'manage_groups':
                    data = model_groups.get(action = 'all_table', start = int(start), length = int(length), search = search, order_column = order_column, order_direction = order_direction)
                    for item in data:
                        item['actions'] = f'<div class="table-actions"><a href="/manage/group/edit?id={item["_id"]}" class="btn-sm bg-outline-primary"><i class="fa-solid fa-pen-to-square"></i></a></div>'
                        item['_id'] = f'<span class="badge bg-primary">{item["_id"]}</span>'

                    data_count = model_groups.get(action = 'all_table_count', search = search, order_column = order_column, order_direction = order_direction)      
                elif v_action == 'manage_careers':
                    data = model_careers.get(action = 'all_table', start = int(start), length = int(length), search = search, order_column = order_column, order_direction = order_direction)
                    for item in data:
                        item['actions'] = f'<div class="table-actions"><a href="/manage/career/edit?id={item["_id"]}" class="btn-sm bg-outline-primary"><i class="fa-solid fa-pen-to-square"></i></a></div>'
                        item['_id'] = f'<span class="badge bg-primary">{item["_id"]}</span>'

                    data_count = model_careers.get(action = 'all_table_count', search = search, order_column = order_column, order_direction = order_direction)               
                elif v_action == 'manage_events':
                    data = model_events.get(action = 'all_table', start = int(start), length = int(length), search = search, order_column = order_column, order_direction = order_direction)
                    for item in data:
                        item['actions'] = f'<div class="table-actions"><a href="/manage/event/edit?id={item["_id"]}" class="btn-sm bg-outline-primary"><i class="fa-solid fa-pen-to-square"></i></a><a href="/manage/event/attendances?id={item["_id"]}" class="btn-sm bg-outline-warning"><i class="fa-solid fa-fingerprint"></i></a></div>'
                        item['name'] = f'<span class="badge bg-primary">{item["name"]}</span>'
                        item['date_start'] = f'<span class="badge bg-primary">{config_convertLocalDate(item["date_start"])}</span>'
                        item['date_final'] = f'<span class="badge bg-primary">{config_convertLocalDate(item["date_final"])}</span>'

                    data_count = model_events.get(action = 'all_table_count', search = search, order_column = order_column, order_direction = order_direction)               
                elif v_action == 'manage_event_attendances':
                    event_id = v_requestForm.get('event_id')
                    if not config_validateForm(form = event_id, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un evento válido e inténtelo de nuevo.'})
                    
                    event_entry = v_requestForm.get('event_entry')
                    if not config_validateForm(form = event_entry, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un valor de entrada o salida válido e inténtelo de nuevo.'})

                    if event_entry == '1':
                        event_entry = True
                    else:
                        event_entry = False

                    careers = v_requestForm.get('careers')
                    if not careers:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione las carreras e inténtelo de nuevo.'})
                    
                    subquery = []
                    careers = str(html.unescape(careers)).replace('\'', '"')
                    careers_json = json.loads(careers)
                    for item in careers_json:
                        career_id = item['career']['_id']
                        semester_id = item['semester']['_id']

                        subquery_data = {
                            'student.career._id': career_id,
                            'student.semester._id': int(semester_id) if semester_id and str(semester_id).isnumeric() else None,
                            'event.$id': int(event_id) if event_id and str(event_id).isnumeric() else None,
                            'entry': event_entry,
                            '$or': [
                                {'student._id': int(search) if search and search.isnumeric() else None},
                                {'student.fullname': {'$regex': config_searchRegex(search), '$options': 'i'}},
                                {'student.semester._id': int(search) if search and search.isnumeric() else None},
                                {'student.semester.name': {'$regex': config_searchRegex(search), '$options': 'i'}},
                                {'student.career._id': {'$regex': config_searchRegex(search), '$options': 'i'}},
                                {'student.career.name': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            ]
                        }
                        subquery.append(subquery_data)

                    data = model_attendances.get(action = 'all_table_filter', subquery = subquery, start = int(start), length = int(length), order_column = order_column, order_direction = order_direction)
                    for item in data:
                        item['student']['fullname'] = f'<div class="d-flex g-10 flex-wrap"><img style="background-image: url(\'/assets/images/students/{item["student"]["img"] if item["student"].get("img") else "user.png"}\')" class="img-table-circle"><div class="d-flex flex-wrap g-5"><p class="w-100"><span class="badge bg-primary">{item["student"]["_id"]}</span></p><p class="w-100">{item["student"]["fullname"]}</p></div></div>'
                        item['regdate'] = f'<span class="badge bg-primary">{config_convertDate(item["regdate"])}</span>'

                    data_count = model_attendances.get(action = 'all_table_filter_count', subquery = subquery, order_column = order_column, order_direction = order_direction)               
                elif v_action == 'manage_event_no_attendances':
                    event_id = v_requestForm.get('event_id')
                    if not config_validateForm(form = event_id, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un evento válido e inténtelo de nuevo.'})

                    event_entry = v_requestForm.get('event_entry')
                    if not config_validateForm(form = event_entry, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un valor de entrada o salida válido e inténtelo de nuevo.'})
                
                    if event_entry == '1':
                        event_entry = True
                    else:
                        event_entry = False

                    careers = v_requestForm.get('careers')
                    if not careers:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione las carreras e inténtelo de nuevo.'})
                    
                    subquery = []
                    careers = str(html.unescape(careers)).replace('\'', '"')
                    careers_json = json.loads(careers)
                    for item in careers_json:
                        career_id = item['career']['_id']
                        semester_id = item['semester']['_id']

                        subquery_data = {
                            'career._id': career_id,
                            'semester._id': int(semester_id) if semester_id and str(semester_id).isnumeric() else None,
                            'attendances': {
                                '$not': {
                                    '$elemMatch': {
                                        'event.$id': int(event_id) if event_id and str(event_id).isnumeric() else None,
                                        'entry': event_entry
                                    }
                                }
                            },
                            '$or': [
                                {'_id': int(search) if search and search.isnumeric() else None},
                                {'fullname': {'$regex': config_searchRegex(search), '$options': 'i'}},
                                {'semester._id': int(search) if search and search.isnumeric() else None},
                                {'semester.name': {'$regex': config_searchRegex(search), '$options': 'i'}},
                                {'career._id': {'$regex': config_searchRegex(search), '$options': 'i'}},
                                {'career.name': {'$regex': config_searchRegex(search), '$options': 'i'}},
                            ]
                        }
                        subquery.append(subquery_data)

                    data = model_students.get(action = 'all_table_attendances', subquery = subquery, start = int(start), length = int(length), order_column = order_column, order_direction = order_direction)
                    for item in data:
                        item['fullname'] = f'<div class="d-flex g-10 flex-wrap"><img style="background-image: url(\'/assets/images/students/{item["img"] if item.get("img") else "user.png"}\')" class="img-table-circle"><div class="d-flex flex-wrap g-5"><p class="w-100"><span class="badge bg-primary">{item["_id"]}</span></p><p class="w-100">{item["fullname"]}</p></div></div>'

                    data_count = model_students.get(action = 'all_table_attendances_count', subquery = subquery, order_column = order_column, order_direction = order_direction)
                elif v_action == 'manage_attendances':
                    data = model_attendances_individual.get(action = 'all_table', start = int(start), length = int(length), search = search, order_column = order_column, order_direction = order_direction)
                    for item in data:
                        item['_id'] = f'<span class="badge bg-primary">{item["_id"]}</span>'
                        item['student_format'] = f'<div class="d-flex g-10 flex-wrap"><img style="background-image: url(\'/assets/images/students/{item["student"]["img"] if item["student"].get("img") else "user.png"}\')" class="img-table-circle"><div class="d-flex flex-wrap g-5"><p class="w-100"><span class="badge bg-primary">{item["student"]["_id"]}</span></p><p class="w-100">{item["student"]["fullname"]}</p></div></div>'
                        item['regdate'] = f'<span class="badge bg-primary">{config_convertDate(item["regdate"])}</span>'

                    data_count = model_attendances_individual.get(action = 'all_table_count', search = search, order_column = order_column, order_direction = order_direction)
                
                return jsonify({'success': True, 'data': data, 'recordsTotal': data_count, 'recordsFiltered': data_count})
            
            #USERS
            elif request.method == 'GET' and path == 'api/web/widget/manage/users':
                total = model_users.get(action = 'count_all')
                return jsonify({'success': True, 'html': render_template('/manage/users.html', total = total)})    
            elif request.method == 'POST' and path == 'api/web/data/manage/users':
                if v_action == 'add':                    
                    username = v_requestForm.get('username')
                    if not config_validateForm(form = username, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un usuario válido e inténtelo de nuevo.'})                          
                                            
                    user_verify = model_users.get(action = 'one_username', username = username)
                    if user_verify:
                        return jsonify({'success': False, 'msg': 'El usuario ya está en uso. Por favor, proporcione un usuario válido e inténtelo de nuevo.'})
                    
                    password = v_requestForm.get('password')
                    if not config_validateForm(form = password, min = 8):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione una contraseña válida con al menos 8 caracteres e inténtelo de nuevo.'})
                    elif not re.search(r'^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@#$%^&+=!])(?!.*\s).{8,}$', password):
                        return jsonify({'success': False, 'msg': 'La contraseña debe contener al menos una letra mayúscula, una letra minúscula, un número, un carácter especial y tener al menos 8 caracteres. Por favor, inténtelo de nuevo.'})
                                  
                    user_id = config_genUniqueID()
                    passw = bcrypt.hash(password)

                    insert = model_users.insert(action = 'one_register', user_id = user_id, username = html.escape(username), password = passw)
                    if not insert:
                        return jsonify({'success': False, 'msg': 'Algo salió mal al agregar. Inténtalo de nuevo. Si el problema persiste, no dude en contactarnos para obtener ayuda.'}) 
                    
                    return jsonify({'success': True, 'msg': 'Se agregó correctamente. Redireccionando...'})     
                elif v_action == 'edit':
                    param_id = v_requestForm.get('id')
                    item = model_users.get(action = 'one_user_id', user_id = param_id)
                    if not item:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione el id válido e inténtelo de nuevo.'}) 

                    username = v_requestForm.get('username')
                    if not config_validateForm(form = username, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un usuario válido e inténtelo de nuevo.'})
                    
                    username = username.replace(' ', '').lower()
                    if item['username'] != username:                  
                        user_verify = model_users.get(action = 'one_username', username = username)
                        if user_verify:
                            return jsonify({'success': False, 'msg': 'El usuario ya está en uso. Por favor, proporcione un usuario válido e inténtelo de nuevo.'})
                    
                    password = v_requestForm.get('password')
                    if not password:
                        passw = item['password']
                    else:
                        if not config_validateForm(form = password, min = 8):
                            return jsonify({'success': False, 'msg': 'Por favor, proporcione una contraseña válida con al menos 8 caracteres e inténtelo de nuevo.'})
                        elif not re.search(r'^(?=.*[A-Z])(?=.*[a-z])(?=.*\d)(?=.*[@#$%^&+=!])(?!.*\s).{8,}$', password):
                            return jsonify({'success': False, 'msg': 'La contraseña debe contener al menos una letra mayúscula, una letra minúscula, un número, un carácter especial y tener al menos 8 caracteres. Por favor, inténtelo de nuevo.'})

                        passw = bcrypt.hash(password)              

                    update = model_users.update(action = 'one', user_id = param_id, username = username, password = passw)
                    if not update:
                        return jsonify({'success': False, 'msg': 'Algo salió mal al editar. Inténtalo de nuevo. Si el problema persiste, no dude en contactarnos para obtener ayuda.'}) 
                    
                    return jsonify({'success': True, 'msg': 'Se editó correctamente. Redireccionando...'})                
            #USER ADD/EDIT
            elif request.method == 'GET' and path == 'api/web/widget/manage/user/add':
                return jsonify({'success': True, 'html': render_template('/manage/user/add.html')})    
            elif request.method == 'GET' and path == 'api/web/widget/manage/user/edit':
                param_id = v_requestArgs.get('id')
                item = model_users.get(action = 'one_user_id', user_id = param_id)
                if item:
                    return jsonify({'success': True, 'html': render_template('/manage/user/edit.html', item = item)})    

            #STUDENTS
            elif request.method == 'GET' and path == 'api/web/widget/manage/students':
                total = model_students.get(action = 'count_all')
                semesters = model_semesters.get(action = 'all')
                careers = model_careers.get(action = 'all')
                groups = model_groups.get(action = 'all')
                return jsonify({'success': True, 'html': render_template('/manage/students.html', total = total, careers = careers, semesters = semesters, groups = groups)})    
            elif request.method == 'POST' and path == 'api/web/data/manage/students':
                if v_action == 'add':
                    student_id = v_requestForm.get('_id')          
                    item = model_students.get(action = 'one_student_id', student_id = student_id)
                    if item:
                        return jsonify({'success': False, 'msg': 'Numero de control en uso. Por favor, proporcione un numero de control válido e inténtelo de nuevo.'}) 
                    
                    file = request.files.get('file')
                    if file and file.filename == '':
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione una imagen válida e inténtelo de nuevo.'})

                    fullname = v_requestForm.get('fullname')
                    if not config_validateForm(form = fullname, min = 1) or not fullname.replace(' ', '').isalpha():
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un nombre completo válido e inténtelo de nuevo.'})        

                    fullname = fullname.upper()

                    semester_id = v_requestForm.get('semester_id')
                    semester = model_semesters.get(action = 'one_semester_id', semester_id = semester_id)
                    if not semester:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un semestre válido e inténtelo de nuevo.'}) 
                    
                    group_id = v_requestForm.get('group_id')
                    group = model_groups.get(action = 'one_group_id', group_id = group_id)
                    if not group:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un grupo válido e inténtelo de nuevo.'}) 
                    
                    career_id = v_requestForm.get('career_id')
                    career = model_careers.get(action = 'one_career_id', career_id = career_id)
                    if not career:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione una carrera válida e inténtelo de nuevo.'}) 
                    
                    img_name = None
                    if file:
                        img_name = str(uuid.uuid4())  + '.png'

                    if file and config_allowedFile(file.filename):
                        filename = os.path.join('static/images/students', img_name)
                        file.save(filename)
                        
                        image = Image.open(filename)
                        webp_filename = os.path.join('static/images/students', img_name)
                        image.save(webp_filename, 'png')
                        image.thumbnail((780, 1080))

                        webp_resized_filename = os.path.join('static/images/students', img_name)
                        image.save(webp_resized_filename, 'png')

                    insert = model_students.insert(action = 'one', student_id = student_id, img = img_name, fullname = fullname, career_id = career_id, group_id = group_id, semester_id = semester_id)
                    if not insert:
                        return jsonify({'success': False, 'msg': 'Algo salió mal al agregar. Inténtalo de nuevo. Si el problema persiste, no dude en contactarnos para obtener ayuda.'}) 
                    
                    return jsonify({'success': True, 'msg': 'Se agregó correctamente. Redireccionando...'})    
                elif v_action == 'edit':
                    param_id = v_requestForm.get('id')
                    item = model_students.get(action = 'one_student_id', student_id = param_id)
                    if not item:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione el id válido e inténtelo de nuevo.'}) 
       
                    file = request.files.get('file')
                    if file and file.filename == '':
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione una imagen válida e inténtelo de nuevo.'})
                    
                    fullname = v_requestForm.get('fullname')
                    if not config_validateForm(form = fullname, min = 1) or not fullname.replace(' ', '').isalpha():
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un nombre completo válido e inténtelo de nuevo.'})        

                    fullname = fullname.upper()

                    semester_id = v_requestForm.get('semester_id')
                    semester = model_semesters.get(action = 'one_semester_id', semester_id = semester_id)
                    if not semester:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un semestre válido e inténtelo de nuevo.'}) 
                    
                    group_id = v_requestForm.get('group_id')
                    group = model_groups.get(action = 'one_group_id', group_id = group_id)
                    if not group:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un grupo válido e inténtelo de nuevo.'}) 
                    
                    career_id = v_requestForm.get('career_id')
                    career = model_careers.get(action = 'one_career_id', career_id = career_id)
                    if not career:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione una carrera válida e inténtelo de nuevo.'}) 
                    
                    img_name = None
                    img_db = item.get('img')
                    if img_db:
                        img_name = img_db
                    
                    if file:                        
                        if img_db:
                            img_name = img_db
                        else:
                            img_name = str(uuid.uuid4())  + '.png'

                    if file and config_allowedFile(file.filename):
                        filename = os.path.join('static/images/students', img_name)
                        file.save(filename)
                        
                        image = Image.open(filename)
                        webp_filename = os.path.join('static/images/students', img_name)
                        image.save(webp_filename, 'png')
                        image.thumbnail((780, 1080))

                        webp_resized_filename = os.path.join('static/images/students', img_name)
                        image.save(webp_resized_filename, 'png')

                    update = model_students.update(action = 'one', student_id = param_id, img = img_name, fullname = fullname, career_id = career_id, group_id = group_id, semester_id = semester_id)
                    if not update:
                        return jsonify({'success': False, 'msg': 'Algo salió mal al editar. Inténtalo de nuevo. Si el problema persiste, no dude en contactarnos para obtener ayuda.'}) 
                    
                    return jsonify({'success': True, 'msg': 'Se editó correctamente. Redireccionando...'})                
                elif v_action == 'delete_fingerprint':
                    param_id = v_requestForm.get('id')
                    item = model_students.get(action = 'one_student_id', student_id = param_id)
                    if not item:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione el id válido e inténtelo de nuevo.'}) 

                    fingerprint_id = v_requestForm.get('fingerprint_id')
                    fingerprint = model_fingerprints.get(action = 'one_fingerprint', fingerprint_id = fingerprint_id)
                    if not fingerprint:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un id de huella válido e inténtelo de nuevo.'}) 

                    delete = model_fingerprints.delete(action = 'one', fingerprint_id = fingerprint_id)
                    if not delete:
                        return jsonify({'success': False, 'msg': 'Algo salió mal al eliminar. Inténtalo de nuevo. Si el problema persiste, no dude en contactarnos para obtener ayuda.'}) 
                    
                    return jsonify({'success': True, 'msg': 'Se eliminó correctamente.'})                           
            #STUDENT ADD/EDIT
            elif request.method == 'GET' and path == 'api/web/widget/manage/student/add':
                semesters = model_semesters.get(action = 'all')
                groups = model_groups.get(action = 'all')
                careers = model_careers.get(action = 'all')
                return jsonify({'success': True, 'html': render_template('/manage/student/add.html', semesters = semesters, groups = groups, careers = careers)})    
            elif request.method == 'GET' and path == 'api/web/widget/manage/student/edit':
                param_id = v_requestArgs.get('id')
                item = model_students.get(action = 'one_student_id', student_id = param_id)
                if item:
                    semesters = model_semesters.get(action = 'all')
                    groups = model_groups.get(action = 'all')
                    careers = model_careers.get(action = 'all')
                    return jsonify({'success': True, 'html': render_template('/manage/student/edit.html', item = item, semesters = semesters, groups = groups, careers = careers)})    
            elif request.method == 'GET' and path == 'api/web/widget/manage/student/fingerprints':
                param_id = v_requestArgs.get('id')
                item = model_students.get(action = 'one_student_id', student_id = param_id)
                if item:
                    total = model_fingerprints.get(action = 'one_student_count', student_id = item['_id'])
                    return jsonify({'success': True, 'html': render_template('/manage/student/fingerprints.html', item = item, total = total)})    
            
            #GROUPS
            elif request.method == 'GET' and path == 'api/web/widget/manage/groups':
                total = model_groups.get(action = 'count_all')
                return jsonify({'success': True, 'html': render_template('/manage/groups.html', total = total)})    
            elif request.method == 'POST' and path == 'api/web/data/manage/groups':
                if v_action == 'add':
                    group_id = v_requestForm.get('_id')
                    if not config_validateForm(form = group_id, min = 1) or not group_id.isalpha():
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione una letra válida e inténtelo de nuevo.'})

                    group_id = group_id.replace(' ', '').upper()
                    group_verify = model_groups.get(action = 'one_group_id', group_id = group_id)
                    if group_verify:
                        return jsonify({'success': False, 'msg': 'La letra del grupo ya está en uso. Por favor, proporcione una letra de grupo válido e inténtelo de nuevo.'})
                    
                    name = v_requestForm.get('name')
                    if not config_validateForm(form = name, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un nombre válido e inténtelo de nuevo.'})

                    insert = model_groups.insert(action = 'one', group_id = group_id, name = html.escape(name))
                    if not insert:
                        return jsonify({'success': False, 'msg': 'Algo salió mal al agregar. Inténtalo de nuevo. Si el problema persiste, no dude en contactarnos para obtener ayuda.'}) 
                    
                    return jsonify({'success': True, 'msg': 'Se agregó correctamente. Redireccionando...'})     
                elif v_action == 'edit':
                    param_id = v_requestForm.get('id')
                    item = model_groups.get(action = 'one_group_id', group_id = param_id)
                    if not item:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione el id válido e inténtelo de nuevo.'}) 

                    name = v_requestForm.get('name')
                    if not config_validateForm(form = name, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un nombre válido e inténtelo de nuevo.'})       

                    update = model_groups.update(action = 'one', group_id = param_id, name = name)
                    if not update:
                        return jsonify({'success': False, 'msg': 'Algo salió mal al editar. Inténtalo de nuevo. Si el problema persiste, no dude en contactarnos para obtener ayuda.'}) 
                    
                    return jsonify({'success': True, 'msg': 'Se editó correctamente. Redireccionando...'})                
            #GROUPS ADD/EDIT
            elif request.method == 'GET' and path == 'api/web/widget/manage/group/add':
                return jsonify({'success': True, 'html': render_template('/manage/group/add.html')})    
            elif request.method == 'GET' and path == 'api/web/widget/manage/group/edit':
                param_id = v_requestArgs.get('id')
                item = model_groups.get(action = 'one_group_id', group_id = param_id)
                if item:
                    return jsonify({'success': True, 'html': render_template('/manage/group/edit.html', item = item)})    

            #CAREERS
            elif request.method == 'GET' and path == 'api/web/widget/manage/careers':
                total = model_careers.get(action = 'count_all')
                return jsonify({'success': True, 'html': render_template('/manage/careers.html', total = total)})    
            elif request.method == 'POST' and path == 'api/web/data/manage/careers':
                if v_action == 'add':
                    career_id = v_requestForm.get('_id')
                    if not config_validateForm(form = career_id, min = 1) or not career_id.isalpha():
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione una abreviación válida e inténtelo de nuevo.'})

                    career_id = career_id.replace(' ', '').upper()
                    career_verify = model_careers.get(action = 'one_career_id', career_id = career_id)
                    if career_verify:
                        return jsonify({'success': False, 'msg': 'La abreviación de la carrera ya está en uso. Por favor, proporcione una abreviación de la carrera válida e inténtelo de nuevo.'})
                    
                    name = v_requestForm.get('name')
                    if not config_validateForm(form = name, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un nombre válido e inténtelo de nuevo.'})

                    insert = model_careers.insert(action = 'one', career_id = career_id, name = html.escape(name))
                    if not insert:
                        return jsonify({'success': False, 'msg': 'Algo salió mal al agregar. Inténtalo de nuevo. Si el problema persiste, no dude en contactarnos para obtener ayuda.'}) 
                    
                    return jsonify({'success': True, 'msg': 'Se agregó correctamente. Redireccionando...'})     
                elif v_action == 'edit':
                    param_id = v_requestForm.get('id')
                    item = model_careers.get(action = 'one_career_id', career_id = param_id)
                    if not item:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione el id válido e inténtelo de nuevo.'}) 

                    name = v_requestForm.get('name')
                    if not config_validateForm(form = name, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un nombre válido e inténtelo de nuevo.'})       

                    update = model_careers.update(action = 'one', career_id = param_id, name = name)
                    if not update:
                        return jsonify({'success': False, 'msg': 'Algo salió mal al editar. Inténtalo de nuevo. Si el problema persiste, no dude en contactarnos para obtener ayuda.'}) 
                    
                    return jsonify({'success': True, 'msg': 'Se editó correctamente. Redireccionando...'})                
            #CAREER ADD/EDIT
            elif request.method == 'GET' and path == 'api/web/widget/manage/career/add':
                return jsonify({'success': True, 'html': render_template('/manage/career/add.html')})    
            elif request.method == 'GET' and path == 'api/web/widget/manage/career/edit':
                param_id = v_requestArgs.get('id')
                item = model_careers.get(action = 'one_career_id', career_id = param_id)
                if item:
                    return jsonify({'success': True, 'html': render_template('/manage/career/edit.html', item = item)})    

            #EVENTS
            elif request.method == 'GET' and path == 'api/web/widget/manage/events':
                total = model_events.get(action = 'count_all')
                return jsonify({'success': True, 'html': render_template('/manage/events.html', total = total)})    
            elif request.method == 'POST' and path == 'api/web/data/manage/events':
                if v_action == 'add':
                    name = v_requestForm.get('name')
                    if not config_validateForm(form = name, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un nombre válido e inténtelo de nuevo.'})

                    date_start = v_requestForm.get('date_start')
                    if not config_validateForm(form = date_start, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione una fecha de entrada válida e inténtelo de nuevo.'})
                    
                    date_final = v_requestForm.get('date_final')
                    if not config_validateForm(form = date_final, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione una fecha de salida válida e inténtelo de nuevo.'})

                    career_semester = None
                    if request.form.getlist('career_semester'):
                        career_semester = request.form.getlist('career_semester')
                    
                    if not career_semester:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione al menos una carrera válida e inténtelo de nuevo.'})
                    
                    event_careers = []
                    for item in career_semester:
                        item_json = json.loads(item)                  
                        
                        career_id = item_json['career_id']
                        career = model_careers.get(action = 'one_career_id', career_id = career_id)
                        if not career:
                            return jsonify({'success': False, 'msg': 'Por favor, proporcione una carrera válida e inténtelo de nuevo.'}) 

                        semester_id = item_json['semester_id']
                        semester = model_semesters.get(action = 'one_semester_id', semester_id = semester_id)
                        if not semester:
                            return jsonify({'success': False, 'msg': 'Por favor, proporcione un semestre válido e inténtelo de nuevo.'})

                        event_json = {
                            'career': {'$ref': 'careers', '$id': career_id},
                            'semester': {'$ref': 'semesters', '$id': int(semester_id)}
                        }
                        
                        event_careers.append(event_json)

                    if not event_careers:
                        return jsonify({'success': False, 'msg': 'Por favor, seleccione carreras válidas e inténtelo de nuevo.'})
                    
                    insert = model_events.insert(action = 'one', name = html.escape(name), date_start = config_convertStringDate(date_start), date_final = config_convertStringDate(date_final), careers = event_careers)
                    if not insert:
                        return jsonify({'success': False, 'msg': 'Algo salió mal al agregar. Inténtalo de nuevo. Si el problema persiste, no dude en contactarnos para obtener ayuda.'}) 
                    
                    return jsonify({'success': True, 'msg': 'Se agregó correctamente. Redireccionando...'})     
                elif v_action == 'edit':
                    param_id = v_requestForm.get('id')
                    item = model_events.get(action = 'one_event_id', event_id = int(param_id) if param_id and param_id.isnumeric() else None)
                    if not item:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione el id válido e inténtelo de nuevo.'}) 

                    name = v_requestForm.get('name')
                    if not config_validateForm(form = name, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un nombre válido e inténtelo de nuevo.'})

                    date_start = v_requestForm.get('date_start')
                    if not config_validateForm(form = date_start, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione una fecha de entrada válida e inténtelo de nuevo.'})
                    
                    date_final = v_requestForm.get('date_final')
                    if not config_validateForm(form = date_final, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione una fecha de salida válida e inténtelo de nuevo.'})

                    career_semester = None
                    if request.form.getlist('career_semester'):
                        career_semester = request.form.getlist('career_semester')
                    
                    if not career_semester:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione al menos una carrera válida e inténtelo de nuevo.'})
                    
                    event_careers = []
                    for item in career_semester:
                        item_json = json.loads(item)                  
                        
                        career_id = item_json['career_id']
                        career = model_careers.get(action = 'one_career_id', career_id = career_id)
                        if not career:
                            return jsonify({'success': False, 'msg': 'Por favor, proporcione una carrera válida e inténtelo de nuevo.'}) 

                        semester_id = item_json['semester_id']
                        semester = model_semesters.get(action = 'one_semester_id', semester_id = semester_id)
                        if not semester:
                            return jsonify({'success': False, 'msg': 'Por favor, proporcione un semestre válido e inténtelo de nuevo.'})

                        event_json = {
                            'career': {'$ref': 'careers', '$id': career_id},
                            'semester': {'$ref': 'semesters', '$id': int(semester_id)}
                        }
                        
                        event_careers.append(event_json)

                    if not event_careers:
                        return jsonify({'success': False, 'msg': 'Por favor, seleccione carreras válidas e inténtelo de nuevo.'})

                    update = model_events.update(action = 'one', event_id = int(param_id), name = name, date_start = config_convertStringDate(date_start), date_final = config_convertStringDate(date_final), careers = event_careers)
                    if not update:
                        return jsonify({'success': False, 'msg': 'Algo salió mal al editar. Inténtalo de nuevo. Si el problema persiste, no dude en contactarnos para obtener ayuda.'}) 
                    
                    return jsonify({'success': True, 'msg': 'Se editó correctamente. Redireccionando...'})
            #EVENT ADD/EDIT
            elif request.method == 'GET' and path == 'api/web/widget/manage/event/add':
                semesters = model_semesters.get(action = 'all')
                careers = model_careers.get(action = 'all')
                return jsonify({'success': True, 'html': render_template('/manage/event/add.html', semesters = semesters, careers = careers)})    
            elif request.method == 'GET' and path == 'api/web/widget/manage/event/edit':
                param_id = v_requestArgs.get('id')
                item = model_events.get(action = 'one_event_id', event_id = int(param_id) if param_id and param_id.isnumeric() else None)
                if item:
                    semesters = model_semesters.get(action = 'all')
                    careers = model_careers.get(action = 'all')
                    return jsonify({'success': True, 'html': render_template('/manage/event/edit.html', item = item, semesters = semesters, careers = careers)})    
            elif request.method == 'GET' and path == 'api/web/widget/manage/event/attendances':
                param_id = v_requestArgs.get('id')
                item = model_events.get(action = 'one_event_id', event_id = int(param_id) if param_id and param_id.isnumeric() else None)
                if item:
                    return jsonify({'success': True, 'html': render_template('/manage/event/attendances.html', item = item)})    

            #ATTENDANCES
            elif request.method == 'GET' and path == 'api/web/widget/manage/attendances':
                total = model_attendances_individual.get(action = 'count_all')
                semesters = model_semesters.get(action = 'all')
                careers = model_careers.get(action = 'all')
                groups = model_groups.get(action = 'all')

                return jsonify({'success': True, 'html': render_template('/manage/attendances.html', total = total, semesters = semesters, careers = careers, groups = groups)})    
            elif request.method == 'POST' and path == 'api/web/data/manage/attendances':
                if v_action == 'export':
                    event_id = v_requestForm.get('event_id')
                    event = model_events.get(action = 'one_event_id', event_id = int(event_id) if event_id and str(event_id).isnumeric() else None)
                    if not config_validateForm(form = event_id, min = 1) or not event:
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un evento válido e inténtelo de nuevo.'})
                    
                    career = v_requestForm.get('career')
                    if not config_validateForm(form = career, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione una carrera válida e inténtelo de nuevo.'})
                    
                    workbook = openpyxl.Workbook()
                    worksheetEntry = workbook.active
                    worksheetEntry.title = 'TouchTrack'
                    worksheetEntry['A1'] = event['name']
                    worksheetEntry['A2'] = 'Numero de control'
                    worksheetEntry['B2'] = 'Nombre completo'
                    worksheetEntry['C2'] = 'Semestre'
                    worksheetEntry['D2'] = 'Carrera'
                    worksheetEntry['E2'] = 'Fecha entrada'
                    worksheetEntry['F2'] = 'Fecha salida'
                    
                    if career == 'all':
                        subquery = []
                        careers = event['careers']
                        for item in careers:
                            career_id = item['career']['_id']
                            semester_id = item['semester']['_id']
                            
                            subquery_data = {
                                'career._id': career_id,
                                'semester._id': int(semester_id) if semester_id and str(semester_id).isnumeric() else None                                
                            }
                            subquery.append(subquery_data)

                        students = model_students.get(action = 'all_table_attendances', subquery = subquery, start = 0, length = 1000, order_column = 'fullname', order_direction = 'asc')

                        subquery = []
                        careers = event['careers']
                        for item in careers:
                            career_id = item['career']['_id']
                            semester_id = item['semester']['_id']
                            
                            subquery_data = {
                                'career._id': career_id,
                                'semester._id': int(semester_id) if semester_id and str(semester_id).isnumeric() else None,
                                'attendances': {
                                    '$elemMatch': {
                                        'event.$id': int(event_id) if event_id and str(event_id).isnumeric() else None,
                                    }                                    
                                }                             
                            }
                            subquery.append(subquery_data)

                        students_2 = model_students.get(action = 'all_table_attendances', subquery = subquery, start = 0, length = 1000, order_column = 'fullname', order_direction = 'asc')

                        i = 3
                        for student in students:                                
                            worksheetEntry[f'A{i}'] = student['_id']
                            worksheetEntry[f'B{i}'] = student['fullname'] 
                            worksheetEntry[f'C{i}'] = student['semester']['name'] 
                            worksheetEntry[f'D{i}'] = student['career']['name']
                            worksheetEntry[f'E{i}'] = 'N/D'
                            worksheetEntry[f'F{i}'] = 'N/D'
                            
                            for student_2 in students_2:
                                if student_2['_id'] == student['_id']:
                                    for attendance in student_2['attendances']:  
                                        if attendance['entry'] == True:
                                            worksheetEntry[f'E{i}'] = config_convertDate(attendance['regdate'])
                                        else:
                                            worksheetEntry[f'F{i}'] = config_convertDate(attendance['regdate'])
                            i += 1
                        
                    else:
                        careers = str(html.unescape(career)).replace('\'', '"')
                        careers_json = json.loads(careers)

                        career_id = careers_json['career']['_id']
                        semester_id = careers_json['semester']['_id']

                        subquery = []                            
                        subquery_data = {
                            'career._id': career_id,
                            'semester._id': int(semester_id) if semester_id and str(semester_id).isnumeric() else None                                
                        }
                        subquery.append(subquery_data)

                        students = model_students.get(action = 'all_table_attendances', subquery = subquery, start = 0, length = 1000, order_column = 'fullname', order_direction = 'asc')

                        subquery = []                            
                        subquery_data = {
                            'career._id': career_id,
                            'semester._id': int(semester_id) if semester_id and str(semester_id).isnumeric() else None,
                            'attendances': {
                                '$elemMatch': {
                                    'event.$id': int(event_id) if event_id and str(event_id).isnumeric() else None,
                                }                                    
                            }                             
                        }
                        subquery.append(subquery_data)

                        students_2 = model_students.get(action = 'all_table_attendances', subquery = subquery, start = 0, length = 1000, order_column = 'fullname', order_direction = 'asc')

                        i = 3                        
                        for student in students:                                
                            worksheetEntry[f'A{i}'] = student['_id']
                            worksheetEntry[f'B{i}'] = student['fullname'] 
                            worksheetEntry[f'C{i}'] = student['semester']['name'] 
                            worksheetEntry[f'D{i}'] = student['career']['name']
                            worksheetEntry[f'E{i}'] = 'N/D'
                            worksheetEntry[f'F{i}'] = 'N/D'
                            
                            for student_2 in students_2:
                                if student_2['_id'] == student['_id']:
                                    for attendance in student_2['attendances']:  
                                        if attendance['entry'] == True:
                                            worksheetEntry[f'E{i}'] = config_convertDate(attendance['regdate'])
                                        else:
                                            worksheetEntry[f'F{i}'] = config_convertDate(attendance['regdate'])
                            i += 1 

                    download_id = str(uuid.uuid4())
                    workbook.save(f'download/{download_id}.xlsx')
                    return jsonify({'success': True, 'msg': 'Se exportó correctamente.', 'download_id': download_id})
                elif v_action == 'export_individual':                  
                    career_id = v_requestForm.get('career')
                    if not config_validateForm(form = career_id, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione una carrera válida e inténtelo de nuevo.'})

                    semester_id = v_requestForm.get('semester')
                    if not config_validateForm(form = semester_id, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un semestre válido e inténtelo de nuevo.'})
                    
                    group = v_requestForm.get('group')
                    if not config_validateForm(form = group, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione un grupo válido e inténtelo de nuevo.'})
                    
                    date_start = v_requestForm.get('date_start')
                    if not config_validateForm(form = date_start, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione una fecha de entrada válida e inténtelo de nuevo.'})
                    
                    date_final = v_requestForm.get('date_final')
                    if not config_validateForm(form = date_final, min = 1):
                        return jsonify({'success': False, 'msg': 'Por favor, proporcione una fecha de salida válida e inténtelo de nuevo.'})
                    
                    workbook = openpyxl.Workbook()
                    worksheetEntry = workbook.active
                    worksheetEntry.title = 'TouchTrack'
                    worksheetEntry['A1'] = 'Asistencias individuales'
                    worksheetEntry['A2'] = 'Numero de control'
                    worksheetEntry['B2'] = 'Nombre completo'
                    worksheetEntry['C2'] = 'Semestre'
                    worksheetEntry['D2'] = 'Carrera'
                    worksheetEntry['E2'] = 'Fecha de registro'                    
                    
                    subquery = []                            
                    subquery_data = {
                        'career._id': career_id,
                        'semester._id': int(semester_id) if semester_id and str(semester_id).isnumeric() else None                                
                    }
                    subquery.append(subquery_data)

                    students = model_students.get(action = 'all_table_attendances_individual', subquery = subquery, start = 0, length = 1000, order_column = 'fullname', order_direction = 'asc')

                    i = 3                    
                    for student in students:
                        worksheetEntry[f'A{i}'] = student['_id']
                        worksheetEntry[f'B{i}'] = student['fullname'] 
                        worksheetEntry[f'C{i}'] = student['semester']['name'] 
                        worksheetEntry[f'D{i}'] = student['career']['name']
                        worksheetEntry[f'E{i}'] = 'N/D'

                        attendances = student['attendances_individual']
                        total_attendances = len(attendances)

                        for index, attendance in enumerate(attendances):
                            regdate = attendance['regdate']

                            date_start_iso = datetime.strptime(date_start, "%Y-%m-%dT%H:%M:%S.%fZ")
                            date_final_iso = datetime.strptime(date_final, "%Y-%m-%dT%H:%M:%S.%fZ")
                            if date_start_iso <= regdate <= date_final_iso:
                                worksheetEntry[f'A{i}'] = student['_id']
                                worksheetEntry[f'B{i}'] = student['fullname'] 
                                worksheetEntry[f'C{i}'] = student['semester']['name'] 
                                worksheetEntry[f'D{i}'] = student['career']['name']
                                worksheetEntry[f'E{i}'] = config_convertDate(regdate)
                            
                                if index < total_attendances - 1:
                                    i += 1
                        
                        i += 1 

                    download_id = str(uuid.uuid4())
                    workbook.save(f'download/{download_id}.xlsx')
                    return jsonify({'success': True, 'msg': 'Se exportó correctamente.', 'download_id': download_id})
            
        if request.method == 'POST' and path == 'api/desktop/data/auth':
            username = v_requestJson.get('username')
            if not config_validateForm(form = username, min = 1):
                return jsonify({'success': False, 'msg': config_desktop_msg('Por favor, proporcione un nombre de usuario válido e inténtelo de nuevo.')})

            password = v_requestJson.get('password')
            if not config_validateForm(form = password, min = 1):
                return jsonify({'success': False, 'msg': config_desktop_msg('Por favor, proporcione una contraseña válida e inténtelo de nuevo.')})
            
            user_verify = model_users.get(action = 'one_username', username = username)
            if not user_verify:
                return jsonify({'success': False, 'msg': config_desktop_msg('Por favor, proporcione un usuario o contraseña válidos válido e inténtelo de nuevo.')})

            if not bcrypt.verify(password, user_verify['password']):
                return jsonify({'success': False, 'msg': config_desktop_msg('Por favor, proporcione un usuario o contraseña válidos válido e inténtelo de nuevo.')})

            return jsonify({'success': True, 'msg': config_desktop_msg('Bienvenido/a a TouchTrack. Abriendo panel...')})
        elif request.method == 'POST' and path == 'api/desktop/data/student':
            if v_action_json == 'get_student':
                student_id = v_requestJson.get('id')
                if not config_validateForm(form = student_id, min = 1):
                    return jsonify({'success': False, 'msg': config_desktop_msg('Por favor, proporcione un numero de control válido e inténtelo de nuevo.')})

                student = model_students.get(action = 'one_student_id', student_id = student_id)
                if not student:
                    return jsonify({'success': False, 'msg': config_desktop_msg('Numero de control no encontado. Por favor, proporcione un numero de control válido e inténtelo de nuevo.')})
                
                img = student.get('img')
                if not img:
                    student['img'] = 'assets/images/user.png'
                else:
                    student['img'] = f'assets/images/students/{student["img"]}'
                
                return jsonify({'success': True, 'msg': config_desktop_msg('Estudiante encontrado.'), 'data': student})
            elif v_action_json == 'get_fingerprints':
                student_id = v_requestJson.get('student_id')
                if not config_validateForm(form = student_id, min = 1):
                    return jsonify({'success': False, 'msg': config_desktop_msg('Por favor, proporcione un numero de control válido e inténtelo de nuevo.')})

                student = model_students.get(action = 'one_student_id', student_id = student_id)
                if not student:
                    return jsonify({'success': False, 'msg': config_desktop_msg('Numero de control no encontado. Por favor, proporcione un numero de control válido e inténtelo de nuevo.')})
                
                fingerprints = model_fingerprints.get(action = 'all_student_fingerprints', student_id = student_id)
                if not fingerprints:
                    return jsonify({'success': False, 'msg': config_desktop_msg('El alumno no tiene huellas registradas.')})

                for item in fingerprints:
                    item['regdate'] = config_convertDate(item['regdate'])

                return jsonify({'success': True, 'msg': config_desktop_msg('Estudiante encontrado.'), 'data': fingerprints})
            elif v_action_json == 'save_fingerprint':
                student_id = v_requestJson.get('student_id')
                if not config_validateForm(form = student_id, min = 1):
                    return jsonify({'success': False, 'msg': config_desktop_msg('Por favor, proporcione un numero de control válido e inténtelo de nuevo.')})

                fingerprint = v_requestJson.get('fingerprint')
                if not config_validateForm(form = fingerprint, min = 1):
                    return jsonify({'success': False, 'msg': config_desktop_msg('Por favor, proporcione una huella válida e inténtelo de nuevo.')})

                student = model_students.get(action = 'one_student_id', student_id = student_id)
                if not student:
                    return jsonify({'success': False, 'msg': config_desktop_msg('Numero de control no encontado. Por favor, proporcione un numero de control válido e inténtelo de nuevo.')})
                
                insert = model_fingerprints.insert(action = 'one', fingerprint_id = str(uuid.uuid4()), fingerprint = fingerprint, student_id = student_id)
                if not insert:
                    return jsonify({'success': False, 'msg': config_desktop_msg('Algo salió mal al agregar. Inténtalo de nuevo. Si el problema persiste, contacte a algun experto.')})
                
                return jsonify({'success': True, 'msg': config_desktop_msg('Huella añadida.'), 'data': student})
            elif v_action_json == 'save_attendance':
                student_id = v_requestJson.get('student_id')
                if not config_validateForm(form = student_id, min = 1):
                    return jsonify({'success': False, 'msg': config_desktop_msg('Por favor, proporcione un numero de control válido e inténtelo de nuevo.')})  

                student = model_students.get(action = 'one_student_id', student_id = student_id)
                if not student:
                    return jsonify({'success': False, 'msg': config_desktop_msg('Numero de control no encontado. Por favor, proporcione un numero de control válido e inténtelo de nuevo.')})             
                                
                events_start = model_events.get(action = 'all_filter_start', career_id = student['career']['_id'], semester_id = student['semester']['_id'])
                if events_start:
                    for item in events_start:
                        consult_attendance = model_attendances.get(action = 'all_student', event_id = item['_id'], student_id = student['_id'], entry = True)
                        if not consult_attendance:
                            insert = model_attendances.insert(action = 'one', student_id = int(student_id), event_id = item['_id'], entry = True)
                            if not insert:
                                return jsonify({'success': False, 'msg': config_desktop_msg('Algo salió mal al agregar entrada del evento. Inténtalo de nuevo. Si el problema persiste, contacte a algun experto.')})
                
                events_final = model_events.get(action = 'all_filter_final', career_id = student['career']['_id'], semester_id = student['semester']['_id'])
                if events_final:
                    for item in events_final:
                        consult_attendance = model_attendances.get(action = 'all_student', event_id = item['_id'], student_id = student['_id'], entry = False)
                        if not consult_attendance:
                            insert = model_attendances.insert(action = 'one', student_id = int(student_id), event_id = item['_id'], entry = False)
                            if not insert:
                                return jsonify({'success': False, 'msg': config_desktop_msg('Algo salió mal al agregar salida del evento. Inténtalo de nuevo. Si el problema persiste, contacte a algun experto.')})                   

                if not events_start and not events_final:
                    return jsonify({'success': False, 'msg': config_desktop_msg('No se encontro un evento para tu carrera. Por favor, inténtelo de nuevo mas tarde.')})          
                    
                return jsonify({'success': True, 'msg': config_desktop_msg('Asistencia correcta.'), 'data': student})        
            elif v_action_json == 'save_attendance_individual':
                student_id = v_requestJson.get('student_id')
                if not config_validateForm(form = student_id, min = 1):
                    return jsonify({'success': False, 'msg': config_desktop_msg('Por favor, proporcione un numero de control válido e inténtelo de nuevo.')})  

                student = model_students.get(action = 'one_student_id', student_id = student_id)
                if not student:
                    return jsonify({'success': False, 'msg': config_desktop_msg('Numero de control no encontado. Por favor, proporcione un numero de control válido e inténtelo de nuevo.')})             
                                
                insert = model_attendances_individual.insert(action = 'one', student_id = int(student_id))                            
                if not insert:
                    return jsonify({'success': False, 'msg': 'Algo salió mal al agregar la asistencia. Inténtalo de nuevo. Si el problema persiste, no dude en contactarnos para obtener ayuda.'})         
                    
                return jsonify({'success': True, 'msg': config_desktop_msg('Asistencia correcta.'), 'data': student})        
        elif request.method == 'POST' and path == 'api/desktop/data/students':
            if v_action_json == 'get_fingerprints':
                fingerprints = model_fingerprints.get(action = 'all_students_fingerprints')
                if not fingerprints:
                    return jsonify({'success': False, 'msg': config_desktop_msg('No hay huellas registradas.')})

                for item in fingerprints:
                    img = item['student'].get('img')
                    if not img:
                        item['student']['img'] = 'assets/images/user.png'
                    else:
                        item['student']['img'] = f'assets/images/students/{item["student"]["img"]}'
                        
                    item['regdate'] = config_convertDate(item['regdate'])
                
                return jsonify({'success': True, 'msg': config_desktop_msg('Estudiante encontrado.'), 'data': fingerprints})         
        
        if request.method == 'POST' and v_config_splitList[0] == 'api':
            return jsonify({'success': True, 'code': 'S404', 'msg': 'Página no encontrada.'}), 404
        elif request.method == 'GET' and v_config_splitList[0] == 'api':
            return jsonify({'success': True, 'html': render_template('/widget/error.html', code = '404', msg = 'Página no encontrada.')}), 404
 
        return render_template('/error.html', code = '404', msg = 'Página no encontrada.'), 404
    except Exception as e:
        print(e)
        if request.method == 'POST' and v_config_splitList[0] == 'api':
            return jsonify({'success': True, 'code': f'S500C{sys.exc_info()[-1].tb_lineno}', 'msg': f'[S500C{sys.exc_info()[-1].tb_lineno}] An error occurred! The bug has been successfully reported and we will be working to fix it.'}), 500
        elif request.method == 'GET' and v_config_splitList[0] == 'api':
            return jsonify({'success': True, 'html': render_template('/widget/error.html', code = '500', msg = f'[S500C{sys.exc_info()[-1].tb_lineno}] An error occurred! The bug has been successfully reported and we will be working to fix it.')}), 500
        
        return render_template('/error.html', code = '500', msg = f'[S500C{sys.exc_info()[-1].tb_lineno}] An error occurred! The bug has been successfully reported and we will be working to fix it.'), 500

@app.template_filter('date_format')
def main_date_format(value):    
    return Markup(config_convertLocalDate(value))

@app.template_filter('datelocal_format')
def main_datelocal_format(value):    
    return Markup(config_convertDatetoT(value))
 
@app.errorhandler(400)
def main_error_400(e):
    if request.method == 'POST':
        return jsonify({'success': False, 'code': 'S400', 'msg': 'Bad request.'}), 400
    
    return render_template('/error.html', code = '400', msg = 'Bad request.'), 400

@app.errorhandler(401)
def main_error_401(e):
    if request.method == 'POST':
        return jsonify({'success': False, 'code': 'S401', 'msg': 'Unauthorized.'}), 401
    
    return render_template('/error.html', code = '404', msg = 'Unauthorized.'), 401

@app.errorhandler(403)
def main_error_403(e):
    if request.method == 'POST':
        return jsonify({'success': False, 'code': 'S403', 'msg': 'Forbidden.'}), 403
    
    return render_template('/error.html', code = '404', msg = 'Forbidden.'), 403

@app.errorhandler(404)
def main_error_404(e):
    if request.method == 'POST':
        return jsonify({'success': False, 'code': 'S404', 'msg': 'Página no encontrada.'}), 404
    
    return render_template('/error.html', code = '404', msg = 'Página no encontrada.'), 404

@app.errorhandler(405)
def main_error_405(e):
    if request.method == 'POST':
        return jsonify({'success': False, 'code': 'S405', 'msg': 'Method not allowed.'}), 405
    
    return render_template('/error.html', code = '404', msg = 'Method not allowed.'), 405

@app.errorhandler(500)
def main_error_500(e):
    if request.method == 'POST':
        return jsonify({'success': False, 'code': 'S500', 'msg': 'An error occurred! The error was reported correctly and we will be working to fix it.'}), 500
    
    return render_template('/error.html', code = '500', msg = f'An error occurred! The bug has been successfully reported and we will be working to fix it.'), 500

@app.errorhandler(503)
def main_error_503(e):
    if request.method == 'POST':
        return jsonify({'success': False, 'code': 'S503', 'msg': 'Service unavailable.'}), 503
    
    return render_template('/error.html', code = '404', msg = 'Service unavailable.'), 503

@app.errorhandler(505)
def main_error_505(e):
    if request.method == 'POST':
        return jsonify({'success': False, 'code': 'S505', 'msg': 'HTTP Version not supported.'}), 505
    
    return render_template('/error.html', code = '404', msg = 'HTTP Version not supported.'), 505

if __name__ == '__main__':
    app.run(host = '0.0.0.0', debug = config_app['debug'], port = 5000)