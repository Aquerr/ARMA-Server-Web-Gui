#!/bin/bash

set -e

export PUID="${PUID:-0}"
export PGID="${PGID:-0}"

echo "Running container as ${PUID}:${PGID}"

if [ "$PUID" = "0" ]; then
    echo "Skipping creation of ${APP_USER} because running as root"
else
  # Create group if it doesn't exist

  getent group "${PGID}" >/dev/null 2>&1 && groupIdExists=true || groupIdExists=false
  getent group "${APP_GROUP}" >/dev/null 2>&1 && groupNameExists=true || groupNameExists=false
  id "${PUID}" >/dev/null 2>&1 && userIdExists=true || userIdExists=false
  id "${APP_USER}" >/dev/null 2>&1 && userNameExists=true || userNameExists=false

  echo "PGID ${PGID} exists: ${groupIdExists}"
  echo "APP_GROUP ${APP_GROUP} exists: ${groupNameExists}"
  echo "PUID ${PUID} exists: ${userIdExists}"
  echo "APP_USER ${APP_USER} exists: ${userNameExists}"

  if [ "${groupIdExists}" = false ]; then
      echo "Group ${PGID} does not exist"
      if [ "${groupNameExists}" = false ]; then
        echo "Creating group ${PGID} ${APP_GROUP}"
        groupadd -g "${PGID}" "${APP_GROUP}"
      else
        groupmod -g "${PGID}" "${APP_GROUP}"
      fi
  else
      echo "Group ${PGID} exists"
      echo "Deleting group ${PGID}"
      delgroup -g "${PGID}"
      if [ "${groupNameExists}" = false ]; then
        echo "Group ${APP_GROUP} does not exist"
        echo "Creating group ${PGID} with name ${APP_GROUP}"
        groupadd -g "${PGID}" "${APP_GROUP}"
      else
        echo "Modifying group ${APP_GROUP} with id ${PGID}"
        delgroup -g "${APP_GROUP}"
        groupadd -g "${PGID}" "${APP_GROUP}"
      fi
  fi

  # Create user if it doesn't exist
  if [ "${userIdExists}" = false ]; then
    if [ "${userNameExists}" = true ]; then
      echo "Modifying user ${APP_USER} with id ${PUID}:${PGID}"
      usermod -u "${PUID}" -g "${PGID}" "${APP_USER}"
    else
      echo "Creating user ${APP_USER} with id ${PUID}:${PGID}"
      useradd \
          -u "${PUID}" \
          -g "${PGID}" \
          -s /bin/sh \
          -m \
          "${APP_USER}"
    fi
  else
    deluser "${PUID}"
    if [ "${userNameExists}" = true ]; then
      echo "Modifying user ${APP_USER} with id ${PUID}:${PGID}"
      usermod -u "${PUID}" -g "${PGID}" "${APP_USER}"
    else
      echo "Creating user ${APP_USER} with id ${PUID}:${PGID}"
      useradd \
          -u "${PUID}" \
          -g "${PGID}" \
          -s /bin/sh \
          -m \
          "${APP_USER}"
    fi
  fi
fi

if [ "$PUID" = "0" ]; then
  echo "Skipping ownership changes because running as root"
else
  directories=("/home/${APP_USER}" "/aswg/config" "/aswg/data" "/aswg/logs" "/aswg/arma-server" "/steamcmd")
  for i in "${directories[@]}" ; do
    echo "Checking ownership of ${i}"
    # Check ownership for /config
    if [ ! -e "${i}" ]; then
      mkdir "${i}"
    fi

    CURRENT_UID=$(stat -c %u "${i}")
    CURRENT_GID=$(stat -c %g "${i}")

    if [ "$CURRENT_UID" -ne "$PUID" ] || [ "$CURRENT_GID" -ne "$PGID" ]; then
      echo "Fixing ownership of ${i}"
      if ! chown -R "$PUID:$PGID" "${i}" 2>/dev/null; then
        echo "Warning: Could not chown ${i}; continuing anyway"
      fi
    else
      echo "${i} already owned by correct UID/GID, skipping chown"
    fi
  done
fi

# Drop privileges (when asked to) if root, otherwise run as current user
if [ "$(id -u)" = "0" ] && [ "${PUID}" != "0" ]; then
  echo "Exec as ${APP_USER}"
  exec gosu "${APP_USER}" "$@"
else
  echo "Exec as current user"
  exec "$@"
fi