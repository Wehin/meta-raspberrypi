DESCRIPTION = "This repository contains the source code for the ARM side \
libraries used on Raspberry Pi. These typically are installed in /opt/vc/lib \
and includes source for the ARM side code to interface to: EGL, mmal, GLESv2,\
vcos, openmaxil, vchiq_arm, bcm_host, WFC, OpenVG."
LICENSE = "Broadcom"
LIC_FILES_CHKSUM = "file://LICENCE;md5=957f6640d5e2d2acfce73a36a56cb32f"

PR = "r4"

PROVIDES = "virtual/libgles2 \
            virtual/egl"
COMPATIBLE_MACHINE = "raspberrypi"

SRCBRANCH = "master"
SRCFORK = "raspberrypi"
SRCREV = "85441185e653347e6b3c2bbc7494f5e29a6ca4a2"

SRC_URI = " \
    git://github.com/${SRCFORK}/userland.git;protocol=git;branch=${SRCBRANCH} \
    file://0001-vcos-headers-extend-paths-to-give-other-packages-bet.patch \
    file://egl.pc.in \
    file://glesv2.pc.in \
"
S = "${WORKDIR}/git"

inherit cmake

EXTRA_OECMAKE = "-DCMAKE_BUILD_TYPE=Release -DCMAKE_EXE_LINKER_FLAGS='-Wl,--no-as-needed'"

# The compiled binaries don't provide sonames.
SOLIBS = "${SOLIBSDEV}"

# Most packages are happy wir mesa > 9.0.0
MESA_VERSION = "9.0.0"

do_install_append() {
    mkdir -p ${D}/${prefix}
    mv ${D}/opt/vc/* ${D}/${prefix}
    rm -rf ${D}/opt

    # pkgconfig
    install -d ${D}${libdir}/pkgconfig
    for pkgconfig_name in egl glesv2 ; do
        sed \
            -e 's:%PREFIX%:${prefix}:g' \
            -e 's:%LIBDIR%:${libdir}:g' \
            -e 's:%INCDIR%:${includedir}:g' \
            -e 's:%VERSION%:${MESA_VERSION}:g' \
             < "${WORKDIR}/${pkgconfig_name}.pc.in" > "${D}/${libdir}/pkgconfig/${pkgconfig_name}.pc"
    done
}

FILES_${PN} += "${libdir}/*${SOLIBS}"
FILES_${PN}-dev = " \
    ${includedir} \
    ${prefix}/src \
    ${libdir}/pkgconfig \
"
FILES_${PN}-doc += "${datadir}/install"

PACKAGE_ARCH = "${MACHINE_ARCH}"

