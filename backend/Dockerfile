FROM webdevops/php-apache:8.4

COPY --from=composer:2 /usr/bin/composer /usr/bin/composer

WORKDIR /app

COPY composer.json composer.lock ./
RUN composer install --no-dev --no-scripts --no-autoloader --prefer-dist

COPY . .

RUN composer dump-autoload --optimize \
    && mkdir -p storage/framework/{sessions,views,cache/data} \
    && chown -R application:application storage bootstrap/cache

COPY docker/queue-worker.conf /opt/docker/etc/supervisor.d/queue-worker.conf

CMD bash -c "php artisan migrate --force && php artisan db:seed --force && php artisan storage:link; supervisord"
