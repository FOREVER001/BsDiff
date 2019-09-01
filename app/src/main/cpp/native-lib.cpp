#include <jni.h>
#include <string>
extern "C" {
   extern int bspatch_main(int argc,char * argv[]);
}


extern "C"
JNIEXPORT void JNICALL
Java_com_tianzhuan_bsdiffdemo_MainActivity_doPatchNative(JNIEnv *env, jobject instance,
                                                         jstring oldApk_, jstring newApk_,
                                                         jstring patch_) {
    const char *oldApk = env->GetStringUTFChars(oldApk_, 0);
    const char *newApk = env->GetStringUTFChars(newApk_, 0);
    const char *patch = env->GetStringUTFChars(patch_, 0);
    char * argv[4]={
        "bspatch",//可随意填写
       const_cast<char *>(oldApk),
       const_cast<char *>(newApk),
       const_cast<char *>(patch)
    };
    bspatch_main(4,argv);

    env->ReleaseStringUTFChars(oldApk_, oldApk);
    env->ReleaseStringUTFChars(newApk_, newApk);
    env->ReleaseStringUTFChars(patch_, patch);
}