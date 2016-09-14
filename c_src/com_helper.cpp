#include "comChat_ChatController.h"
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <termios.h>
#include <string.h> // needed for memset

termios termios_init()
{
  struct termios tio;
  tio.c_iflag=0;
  tio.c_oflag=0;
  tio.c_cflag=CS8|CREAD|CLOCAL;           // 8n1, see termios.h for more information
  tio.c_lflag=0;
  tio.c_cc[VMIN]=1;
  tio.c_cc[VTIME]=5;
  return tio;
}

JNIEXPORT jint JNICALL Java_comChat_ChatController_initPort(JNIEnv *env, jclass myclass, jstring portName)
{
  struct termios tio;
  int tty_fd;
  bool isCopy = false;
  const char *port_name = env->GetStringUTFChars(portName, (jboolean*)(&isCopy));
  memset(&tio,0,sizeof(tio));
  tio = termios_init();

  tty_fd=open(port_name, O_RDWR | O_NONBLOCK);      
  cfsetospeed(&tio,B115200);            // 115200 baud
  cfsetispeed(&tio,B115200);            // 115200 baud
  tcsetattr(tty_fd,TCSANOW,&tio);
  return tty_fd;
}

JNIEXPORT void JNICALL Java_comChat_ChatController_sendMessage(JNIEnv *env, jclass myclass, jint portDescr, jstring javaMessage, jint jLength)
{
  bool isCopy = false;
  const char *message = env->GetStringUTFChars(javaMessage, (jboolean*)(&isCopy));
  int tty_fd = (int)portDescr;
  int len = (int) jLength;
	write(tty_fd, message, len);
	char c = 10;
	write(tty_fd, &c, 1);
}

JNIEXPORT void JNICALL Java_comChat_ChatController_closePort(JNIEnv *env, jclass myclass, jint portDescr)
{
  int tty_fd = (int)portDescr;
  close(tty_fd);
}

JNIEXPORT jchar JNICALL Java_comChat_ChatController_readSymbol(JNIEnv *env, jclass myclass, jint portDescr)
{
  int tty_fd = (int)portDescr;
  char c;
  if (read(tty_fd, &c, 1) > 0) return (jchar)c;
  else return (jchar)0;
}