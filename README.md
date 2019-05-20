# S2GM Toolbox
Collection of tools of the C3SLOT5 project

To create the run configuration in IDEA follow these steps:
1. Create a **Jar Application** configuration
1. As **Path to JAR** choose `<SNAP_INSTALL_DIR>\snap\modules\ext\org.esa.snap.snap-rcp\org-esa-snap\snap-main.jar`
1. As **VM options** set: `-Dsnap.debug=true -Dsun.java2d.noddraw=true -Dsun.awt.nopixfmt=true -Dsun.java2d.dpiaware=false -Dorg.netbeans.level=INFO -Xmx10G -Dsnap.jai.tileCacheSize=4000`
1. The **Program arguments** should be: `--clusters "<C3SLOT5_PROJECT_DIR>/c3slot5-compare-composite/target/nbm/netbeans/s3tbx" --patches "<C3SLOT5_PROJECT_DIR>//c3slot5-compare-composite/$/target/classes" --userdir "<C3SLOT5_PROJECT_DIR>/c3slot5-compare-composite/.snap"`
1. The **Working dir** should be: `<SNAP_INSTALL_DIR>`   
