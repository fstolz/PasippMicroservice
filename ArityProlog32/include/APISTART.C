#include <stdio.h>
#include <setjmp.h>
#include <apctype.h>




#define USHORT unsigned short
#define ULONG unsigned long
#define MAKEULONG(l, h) ((ULONG)(((USHORT)(l)) | ((ULONG)((USHORT)(h))) << 16))
#define LOUSHORT(l)	((USHORT)(ULONG)(l))

extern int SYSAPI prologmain(void);
extern int SYSAPI prologrestart(void);

/* Symbol must be defined and initialized */
int LOAD_ARITY = -1;

#if defined(TARG_OS2)
static int (* SYSAPI prologptr)(void) = prologmain;
#elif defined(TARG_WIN32)
static int (SYSAPI * prologptr)(void) = prologmain;
#endif
jmp_buf restart_buf;

/* Event handler for Prolog events */
long SYSAPI EventHandler(int event, long l1, long l2)
{
    switch (event)
    {
	case EV_FILEERROR:
	    PrologDefEvHdlr (event, l1, l2);

	case EV_ABORT:
	case EV_POISON:
	    prologptr = prologrestart;
	    longjmp(restart_buf, 1);
	    break;

	case EV_HALT:
	    return MAKEULONG(EVR_ENDPROCESS,LOUSHORT(l1));

	case EV_EXERROR:
	    PrologDefEvHdlr (event, l1, l2);
	    if (LOUSHORT(l1) >= 200)
	    {
		prologptr = prologrestart;
		longjmp(restart_buf, 1);
	    }
	    return MAKEULONG(EVR_ENDPROCESS, 2);
    }
    return PrologDefEvHdlr(event, l1, l2);
}


/*   C's main routine.  Return value is DOS error code:
	0 - main/0 succeeded.
	1 - main/0 failed.
	2 - Startup error occurred
*/

int main(void)
{
    unsigned int i;

    setjmp(restart_buf);	    /* For transferring control */

    i = PrologInitProcess (0, 0, 0);
    if (i == 0)
	i = PrologInitThread (IT_FILLAREA, NULL);
    if (i)
	{
	printf("\n\n%Fs\n", PrologErrorMessage(i));
	return(2);
	}

    PrologRegEvHdlr((PENVHDLR)EventHandler);

    i = (*prologptr)();

    endprolog();

    return !i;

}
