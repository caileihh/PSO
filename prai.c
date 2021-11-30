#include <stdio.h>
#include <stdlib.h>
#include <string.h>
int main(int argc, char * argv[]) {
    char command_esedbexport[10000];

//    char *command_esedbexport;
    strcpy(command_esedbexport,"cd /home/eda210506 && java -jar untitled.jar ");
    strcat(command_esedbexport,argv[1]);
    strcat(command_esedbexport," ");
    strcat(command_esedbexport,argv[2]);
    strcat(command_esedbexport," ");
    strcat(command_esedbexport,"/home/eda210506/ModuleResult.txt");
    system(command_esedbexport);
//    printf("%s",command_esedbexport);
//    for(int i=1;i<argc;i++) printf("%s\n",argv[i]);
}
