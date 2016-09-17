#include "comChat_PortThread.h"
#include "jni.h"
#include "jni_md.h"
#include <stdlib.h>
#include <stdio.h>
#include <unistd.h>
#include <fcntl.h>
#include <termios.h>
#include <string.h> // needed for memset

speed_t normalize_speed(int speed)
{
  switch(speed)
  {
    case 0:
      return B0;
    case 50:
      return B50;
    case 75:
      return B75;
    case 110:
      return B110;
    case 134:
      return B134;
    case 150:
      return B150;
    case 200:
      return B200;
    case 300:
      return B300;
    case 600:
      return B600;
    case 1200:
      return B1200;
    case 1800:
      return B1800;
    case 2400:
      return B2400;
    case 4800:
      return B4800;
    case 9600:
      return B9600;
    case 19200:
      return B19200;
    case 38400:
      return B38400;
    case 57600:
      return B57600;
    case 115200:
      return B115200;
    case 230400:
      return B230400;
  }
  return B0;
}

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

JNIEXPORT jint JNICALL Java_comChat_PortThread_initPort(JNIEnv *env, jobject myobj, jstring portName)
{
  struct termios tio;
  int tty_fd;
  bool isCopy = false;
  const char *port_name = env->GetStringUTFChars(portName, (jboolean*)(&isCopy));
  memset(&tio,0,sizeof(tio));
  tio = termios_init();
  tio.c_cflag = tio.c_cflag | PARENB;
  tty_fd=open(port_name, O_RDWR | O_NONBLOCK);      
  cfsetospeed(&tio,B115200);            // 115200 baud
  cfsetispeed(&tio,B115200);            // 115200 baud
  tcsetattr(tty_fd,TCSANOW,&tio);
  return tty_fd;
}

JNIEXPORT void JNICALL Java_comChat_PortThread_sendMessage(JNIEnv *env, jobject myobj, jint portDescr, jstring javaMessage, jint jLength)
{
  bool isCopy = false;
  const char *message = env->GetStringUTFChars(javaMessage, (jboolean*)(&isCopy));
  int tty_fd = (int)portDescr;
  int len = (int) jLength;
	write(tty_fd, message, len);
	char c = 10;
	write(tty_fd, &c, 1);
}

JNIEXPORT void JNICALL Java_comChat_PortThread_closePort(JNIEnv *env, jobject myobj, jint portDescr)
{
  int tty_fd = (int)portDescr;
  close(tty_fd);
}

JNIEXPORT jchar JNICALL Java_comChat_PortThread_readSymbol(JNIEnv *env, jobject myobj, jint portDescr)
{
  int tty_fd = (int)portDescr;
  char c;
  if (read(tty_fd, &c, 1) > 0) return (jchar)c;
  else return (jchar)0;
}

JNIEXPORT void JNICALL Java_comChat_PortThread_setOptions(JNIEnv *env, jobject myobj, jint portDescr, 
  jint inBaud, jint outBaud, jboolean parityBit)
{
  struct termios tio = termios_init();
  int tty_fd = (int)portDescr;
  int in_baud = (int)inBaud;
  int out_baud = (int)outBaud;
  if ((bool)parityBit)
    tio.c_cflag = tio.c_cflag | PARENB;
  cfsetispeed(&tio, normalize_speed(in_baud));
  cfsetospeed(&tio, normalize_speed(out_baud));
  tcsetattr(tty_fd, TCSANOW, &tio);
}
