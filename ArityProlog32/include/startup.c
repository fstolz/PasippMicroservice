#include <stdio.h>
#include <apctype.h>

/* define the symbol RESTART_PROCESSING to enable a call to
   restart/0 on control-c or control-break
*/

/* SYSAPI entrypoint to main/0 */
extern int SYSAPI prologmain(void);

/* Symbol must be defined and initialized */
int LOAD_ARITY = -1;

#ifdef RESTART_PROCESSING
#include <setjmp.h>
int restart_flag = 0;
extern int SYSAPI prologrestart(void);
jmp_buf restart_buf;

/* Event handler for Prolog events */
long SYSAPI EventHandler(int event, long l1, long l2)
{
unsigned long *pdw;

    if (event == EV_ABORT || event == EV_POISON)
	{
	pdw = (unsigned long *)prologrestart;
#ifdef TARG_OS2
	if (*pdw == 0xFFFFFFFF)
	    restart_flag = 2;
	else
	    restart_flag = 1;
#endif
#ifdef TARG_WIN32
	if ((*pdw & 0xFFFF) == 0x25FF)
	    restart_flag = 2;
	else
	    restart_flag = 1;
#endif

	longjmp(restart_buf, 1);
	}

    return PrologDefEvHdlr(event, l1, l2);
}
#endif


/*   C's main routine.  Return value is DOS error code:
	0 - main/0 succeeded.
	1 - main/0 failed.
	2 - Startup error occurred
*/
int main(void)
{
    unsigned int i;

#ifdef RESTART_PROCESSING
    setjmp(restart_buf);	    /* For transferring control */
#endif

    i = initprolog(0, 0, 0);

/* If you need to do statistics/2 on local, global, or trail, change
    as follows:

    i = PrologInitProcess (0, 0, 0);
    if (i == 0)
	i = PrologInitThread (IT_FILLAREA, NULL);

*/

    if (i)
	{				/* On startup error:		*/
	fprintf(stderr, "\n\n%s\n", PrologErrorMessage(i));
	return 2;			/*   return Dos error code 2	*/
	}
	
#ifdef RESTART_PROCESSING
    PrologRegEvHdlr((PENVHDLR)EventHandler);
    switch (restart_flag)
    {
    case 0:
	i = prologmain();
	break;
    case 1:
	i = prologrestart();
	break;
    case 2:
	i = 5;
	break;
    }
#else
    i = prologmain();
#endif

    endprolog();
	
    return !i;
}
	
