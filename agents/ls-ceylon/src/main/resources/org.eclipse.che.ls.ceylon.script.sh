#
# Copyright (c) 2012-2017 Codenvy, S.A.
# All rights reserved. This program and the accompanying materials
# are made available under the terms of the Eclipse Public License v1.0
# which accompanies this distribution, and is available at
# http://www.eclipse.org/legal/epl-v10.html
#
# Contributors:
#   Codenvy, S.A. - initial API and implementation
#


#########################
### Install Ceylon LS ###
#########################

curl -s https://ceylon-lang.org/download/dist/1_3_2 | tar xzf - -C ${LS_DIR}

cd ${LS_DIR} && ${SUDO} pip3 install --process-dependency-links .

touch ${LS_LAUNCHER}
chmod +x ${LS_LAUNCHER}
echo "pyls" > ${LS_LAUNCHER}
