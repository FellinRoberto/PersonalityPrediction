
#include "my_ini.h"

/*imposta il buffer con il valore di una variabile della sezione del file
	
	file_name: nome del file .ini in cui sono i parametri
	section: la sezione del file .ini in cui si vuole cercare
	var: la variabile della sezione di cui si vuole il valore
	buffer: il buffer in cui viene restituita la stringa con l'eventuale valore
	NOTE: section deve contenere le parantesi quadre (es: [Section X])
*/


//#define TEST

void getValue(FILE *fileINI, char *section, char *var, char *buffer)
{
	char line[DIM_BUF_MAX];
	char *punt_section;
	char *punt_var;
	char *punt_app;
	int section_find = 0;
	int var_find = 0;
	int end_section = 0;
	int i=0;
	
	rewind(fileINI);//setta il file pointer all'inizio del file

	strcpy(buffer, "");//azzera il buffer
	
	while(fgets(line, DIM_BUF_MAX, fileINI) != NULL && section_find != 1)//legge riga per riga il file
	{
		if(line[0]!=';' && line[0]!='#' && line[0]!=' ' && line[0]!='\n')//la linea contiene un commento o è vuota
		{
			if(line[0]=='[')//la linea contiene una sezione
			{
				if((punt_section = strstr (line, section)) != NULL)//se è la sezione che cerca
				{ 
					punt_app = punt_section;
					punt_app = punt_app + (strlen(section) - 1);
					//verifica che non ci siano caratteri/numeri prima e dopo il nome della sezione (es: [y] e [yX] e [Xy] )
					if((*punt_app) == ']' && line[1]==section[1])
					{
						section_find=1;
						while(fgets(line, DIM_BUF_MAX, fileINI) != NULL && !end_section)//legge riga per riga il file
						{
							if(line[0] != ';' && line[0] != '#')//la linea contiene un commento
							{
								if(line[0] == '[')//inizia un'altra sezione
									end_section = 1;
								
								if(!end_section && (punt_var = strstr (line, var)) != NULL)//se è la variabile che cerca
								{
									punt_app = punt_var;
									punt_app = punt_app + strlen(var);
									
									//verifica che non ci siano caratteri/numeri prima e dopo il nome della variabile (es: y e yX e Xy)
									if(line[0]==var[0] && ((*punt_app) == ' ' ||  (*punt_app) == '='))
									{
										var_find = 1;
										punt_app = punt_var;
										while((*punt_app)!= '=')//cerco il segno '='
											punt_app++;
										punt_app++;//carattere successivo all'uguale
										
										while((*punt_app) == ' ')//cerco il valore effettivo della variabile saltando gli spazi
											punt_app++;
										
										while((*punt_app) != '\n')
										{
											buffer[i] = (*punt_app);
											i++;
											punt_app++;
										}
										buffer[i]='\0';
									}
								}
							}
						}
					}
				}
			}
		}
	}
	
	/*TEST
	if(section_find==0)
		printf("La sezione %s non e' presente nel file!\n", section);
	else
	{
		if(var_find == 0)
			printf("La variabile %s non esiste nella sezione %s!\n", var, section);
	}
	*/
}

//restituisce il numero di sezioni 'nameVec' nel file
int getNumVector(FILE *fileINI, char *nameVec)
{
	char line[DIM_BUF_MAX];
	int num_vector = 0;
	char section[128];
	
	strcpy(section, "[");
	strcat(section, nameVec);
	
	rewind(fileINI);
	
	while(fgets(line, DIM_BUF_MAX, fileINI) != NULL )//legge riga per riga il file
	{
		if(line[0]!=';' && line[0]!='#')//la linea contiene un commento
		{
			if((strstr (line, section)) != NULL)//se è la sezione che cerca
				num_vector++;
		}
	}
	
	return num_vector;
}

#ifdef TEST

#include <stdio.h>
#include <stdlib.h>

struct param_vec{
  long t;
  long d;
  double g;
  double s;
  double r;
  char u[64];
  double L;
  char C[64];
  double M;
  long N;
  double weight;
};

int main(int argc, char *argv[])
{
  char valore_res[32];
  struct param_vec *pv[40];
  char sezione [64];
  int i;
  char num[4];
	
	for(i=0;i<40;i++)
	{
		pv[i] = (struct param_vec*)malloc(sizeof(struct param_vec));
		if(pv[i]==NULL)
		{
			printf("Errore nella malloc!\n");
			exit(1);
		}
		
		strcpy(sezione, "[Vector ");
    
//    itoa(i, num, 10);
    sprintf(num,"%d",i);
    if(i<10)
    	strncat(sezione, num, 1);
    else if(i<100)
    	strncat(sezione, num, 2);
    strcat(sezione, "]");
		
		getValue("test.ini", sezione, "par_t", valore_res);
		pv[i]->t = atol(valore_res);
		getValue("test.ini",sezione , "par_d", valore_res);
		pv[i]->d = atol(valore_res);
		getValue("test.ini",sezione , "par_g", valore_res);
		pv[i]->g = atof(valore_res);
		getValue("test.ini",sezione , "par_s", valore_res);
		pv[i]->s = atof(valore_res);
		getValue("test.ini",sezione , "par_r", valore_res);
		pv[i]->r = atof(valore_res);
		getValue("test.ini",sezione , "par_u", valore_res);
		strcpy(pv[i]->u, valore_res);
		getValue("test.ini",sezione , "par_L", valore_res);
		pv[i]->L = atof(valore_res);
		getValue("test.ini",sezione , "par_C", valore_res);
		strcpy(pv[i]->C, valore_res);
		getValue("test.ini",sezione , "par_M", valore_res);
		pv[i]->M = atof(valore_res);
		getValue("test.ini",sezione , "par_N", valore_res);
		pv[i]->N = atol(valore_res);
		getValue("test.ini",sezione , "par_weight", valore_res);
		pv[i]->weight = atof(valore_res);
		
	}
	
	for(i=0;i<40;i++)
  {
		printf("pv[%d].t: %ld\n", i, pv[i]->t);
    printf("pv[%d].d: %ld\n", i, pv[i]->d);
    printf("pv[%d].g: %f\n", i, pv[i]->g);
    printf("pv[%d].s: %f\n", i, pv[i]->s);
    printf("pv[%d].r: %f\n", i, pv[i]->r);
    printf("pv[%d].u: %s\n", i, pv[i]->u);
    printf("pv[%d].L: %f\n", i, pv[i]->L);
    printf("pv[%d].C: %s\n", i, pv[i]->C);
    printf("pv[%d].M: %f\n", i, pv[i]->M);
    printf("pv[%d].N: %ld\n", i, pv[i]->N);
    printf("pv[%d].weight: %f\n", i, pv[i]->weight);
    fflush(stdout);
    free(pv[i]);
  }
  
  system("PAUSE");	
  return 0;
}

#endif
