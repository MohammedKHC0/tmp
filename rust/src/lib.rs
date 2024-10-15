use std::fs::File;

use inotify::{Inotify, WatchMask};
use jni::{objects::{JClass, JString}, sys::jint, JNIEnv};
use jni_fn::jni_fn;
use tar::Archive;


#[jni_fn("com.shadow3.codroid.jni.NativeTarHelper")]
pub fn untar(mut env: JNIEnv, _: JClass, tar_path: JString, target_path: JString) {
    let tar_path: String = env.get_string(&tar_path).unwrap().into();
    let target_path: String = env.get_string(&target_path).unwrap().into();

    let tar = File::open(tar_path).unwrap();
    Archive::new(tar).unpack(target_path).unwrap();
}

#[jni_fn("com.shadow3.codroid.jni.NativeInotifyHelper")]
pub fn waitUntilUpdate(mut env: JNIEnv, _: JClass, path: JString) -> jint {
    let path: String = env.get_string(&path).unwrap().into();
    let Ok(mut inotify) = Inotify::init() else {
        return 1;
    };

    if inotify.watches().add(path, WatchMask::ALL_EVENTS).is_err() {
        return 2;
    }
    let _ = inotify.read_events_blocking(&mut []);
    0
}
