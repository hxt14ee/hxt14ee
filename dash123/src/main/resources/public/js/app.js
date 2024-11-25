document.addEventListener('DOMContentLoaded', function () {
    var calendarEl = document.getElementById('calendar');

    if (calendarEl) {
        var calendar = new FullCalendar.Calendar(calendarEl, {
            initialView: 'dayGridMonth', // Отображение по умолчанию - месяц
            locale: 'ru', // Локализация на русский язык
            headerToolbar: {
                left: 'prev,next', // Кнопки переключения месяца
                center: 'title', // Заголовок
                right: '' // Убираем кнопки масштаба
            },
            height: 'auto', // Автоматическая высота
            contentHeight: 600, // Максимальная высота календаря
            expandRows: true, // Равномерное распределение строк
            events: '/api/events', // Эндпоинт для загрузки событий
            editable: false,
            selectable: true,

            eventMouseEnter: function (info) {
                // Создаем всплывающее окно
                var tooltip = document.createElement('div');
                tooltip.className = 'tooltip';
                tooltip.innerHTML = `
                    <strong>${info.event.title}</strong><br>
                    Начало: ${info.event.start.toLocaleString()}<br>
                    Окончание: ${info.event.end ? info.event.end.toLocaleString() : 'Не указано'}<br>
                    ${info.event.extendedProps.description || ''}
                `;

                // Добавляем стили
                tooltip.style.position = 'absolute';
                tooltip.style.background = '#333';
                tooltip.style.color = '#fff';
                tooltip.style.padding = '10px';
                tooltip.style.borderRadius = '5px';
                tooltip.style.boxShadow = '0 2px 5px rgba(0, 0, 0, 0.3)';
                tooltip.style.fontSize = '0.9rem';
                tooltip.style.zIndex = '1000';
                tooltip.style.whiteSpace = 'nowrap';

                document.body.appendChild(tooltip);

                // Позиционируем подсказку возле курсора
                function positionTooltip(event) {
                    tooltip.style.left = (event.pageX + 10) + 'px';
                    tooltip.style.top = (event.pageY + 10) + 'px';
                }
                positionTooltip(info.jsEvent);

                // Удаляем подсказку при выходе мыши
                info.el.addEventListener('mouseleave', function () {
                    tooltip.remove();
                });

                // Обновляем позицию при движении мыши
                info.el.addEventListener('mousemove', positionTooltip);
            },

            eventClick: function (info) {
                // Всплывающее окно с информацией о событии
                alert(
                    'Событие: ' + info.event.title + '\n' +
                    'Начало: ' + info.event.start.toLocaleString() + '\n' +
                    'Окончание: ' + (info.event.end ? info.event.end.toLocaleString() : 'Не указано') + '\n' +
                    'Описание: ' + (info.event.extendedProps.description || 'Нет описания')
                );
            },

            eventContent: function (arg) {
                // Кастомизация отображения события
                let customHtml = `
                    <b>${arg.event.title}</b><br>
                    <span>${arg.event.extendedProps.description || ''}</span>
                `;
                return { html: customHtml };
            }
        });

        calendar.render();

        // Обработчик для добавления события через форму
        document.getElementById('addEventForm').addEventListener('submit', function (e) {
            e.preventDefault(); // Предотвращаем стандартную отправку формы

            const title = document.getElementById('eventTitle').value.trim();
            const start = document.getElementById('eventStart').value;
            const end = document.getElementById('eventEnd').value;

            // Валидация обязательных полей
            if (!title || !start) {
                alert('Пожалуйста, заполните обязательные поля: Заголовок и Начало события.');
                return;
            }

            // Подготовка данных для отправки
            const formData = new URLSearchParams();
            formData.append('title', title);
            formData.append('start', start);
            formData.append('end', end);

            // Отправка POST-запроса для добавления события
            fetch('/addEvent', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                },
                body: formData
            })
                .then(response => {
                    if (response.redirected) {
                        // Если сервер перенаправляет, следуем перенаправлению
                        window.location.href = response.url;
                    } else if (response.ok) {
                        // Успешное добавление события
                        alert('Событие добавлено успешно.');
                        calendar.refetchEvents(); // Обновляем события в календаре
                        document.getElementById('addEventForm').reset(); // Очищаем форму
                    } else {
                        // Обработка ошибок от сервера
                        return response.text().then(text => { throw new Error(text); });
                    }
                })
                .catch(error => {
                    console.error('Ошибка при добавлении события:', error);
                    alert('Произошла ошибка при добавлении события: ' + error.message);
                });
        });

        // Добавление фильтров для событий
        document.getElementById('filterTasks').addEventListener('click', () => {
            calendar.getEvents().forEach(event => {
                if (event.extendedProps.groupId !== 'tasks') {
                    event.setProp('display', 'none');
                } else {
                    event.setProp('display', 'auto');
                }
            });
        });

        document.getElementById('filterSchedules').addEventListener('click', () => {
            calendar.getEvents().forEach(event => {
                if (event.extendedProps.groupId !== 'schedules') {
                    event.setProp('display', 'none');
                } else {
                    event.setProp('display', 'auto');
                }
            });
        });

        document.getElementById('resetFilters').addEventListener('click', () => {
            calendar.getEvents().forEach(event => {
                event.setProp('display', 'auto'); // Показываем все события
            });
        });
    }
});
