# Copyright 1999-2006 Gentoo Foundation
# Distributed under the terms of the GNU General Public License v2

inherit java-pkg

MY_PN=${PN}

DESCRIPTION="JIndex indexer and gnome search client"
# http://download.berlios.de/jindex/JIndex.tar.gz
SRC_URI=http://download.berlios.de/jindex/JIndex.tar.gz"
HOMEPAGE="http://http://jindex.berlios.de/index.php/Main_Page/"
LICENSE="LGPL"
SLOT="2.0"
KEYWORDS="~amd64 ~x86"

RDEPEND=">=virtual/jdk-1.5"

DEPEND="~dev-java/glib-java-0.2.3
		~dev-java/cairo-java-1.0.2
		~dev-java/libgtk-java-2.8.3
		~dev-java/libgnome-java-2.12.1
		~dev-java/libglade-java-2.12.2
		~dev-java/libgconf-java-2.12.1
		!<dev-java/java-gnome-2.8
		>dev-java/lucene-1.4.3"
S=${WORKDIR}/mozilla

pkg_setup() {
}

src_install() {
	declare JINDEX_HOME=/opt/jindex

	# Install mozilla in /opt
	dodir ${JINDEX_HOME%/*}
	mv ${S} ${D}${JINDEX_HOME}


	# Install icon and .desktop for menu entry
	insinto /usr/share/pixmaps
	doins ${FILESDIR}/jindex-icon.png
	insinto /usr/share/applications
	doins ${FILESDIR}/jindex.desktop
}

pkg_preinst() {
	declare JINDEX_HOME=/opt/jindex
	# Remove entire installed instance to prevent all kinds of
	# problems... see bug 44772 for example
	rm -rf "${ROOT}${JINDEX_HOME}"
}

pkg_postinst() {
}

pkg_postrm() {
}
