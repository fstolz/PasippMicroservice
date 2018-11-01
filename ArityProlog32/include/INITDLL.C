#if defined(TARG_OS2)

#define  INCL_DOSMODULEMGR
#define  INCL_DOSPROCESS
#include <os2.h>
#include <apctype.h>
 
/*  _CRT_init is the C run-time environment initialization function.
 *  It will return 0 to indicate success and -1 to indicate failure.
 */
int _CRT_init(void);

#ifdef STATIC_LINK
 
/*  _CRT_term is the C run-time environment termination function.
 *  It only needs to be called when the C run-time functions are
 *  statically linked.
 */
void _CRT_term(void);

#else
 
/*  A clean up routine registered with DosExitList must be used if
 *  runtime calls are required and the runtime is dynamically linked.
 *  This will guarantee that this clean up routine is run before the
 *  library DLL is terminated.	If you need to do termination that
 *  requires the 'C' runtime environment, uncomment out the following
 *  line:
 */

/* #define EXITLIST_PROCESSING */

#ifdef EXITLIST_PROCESSING
static void _System cleanup (ULONG ulReason);
#endif

#endif


ULONG _System _DLL_InitTerm(HMODULE hModule, ULONG ulFlag)
{
APIRET rc;
 
/*   If ulFlag is zero then the DLL is being loaded.  If ulFlag is
 *   nonzero it is being unloaded.  Remember that the DLL must be
 *   linked with INITINSTANCE and TERMINSTANCE
 */

    switch (ulFlag)
    {
    case 0:
 
/*  The C run-time environment initialization function must be
 *  called before any calls to C run-time functions that are not
 */

	if (_CRT_init() == -1)
	    return 0UL;

#ifndef  STATIC_LINK
#ifdef EXITLIST_PROCESSING

	if (rc = DosExitList(0x0000FF00|EXLST_ADD, cleanup))
	    return 0UL;

#endif
#endif

/*  Now we add our visible definitions */

	DllVisibles (1);
	break;


    case 1:

/*  Remove our visible definitions */

	DllVisibles (0);
#ifdef	STATIC_LINK
	_CRT_term ();
#endif

	break;


    default:
	return 0UL;
    }
 
/* A non-zero value must be returned to indicate success. */
 
    return 1UL;
}


#ifndef  STATIC_LINK
#ifdef	 EXITLIST_PROCESSING
static void cleanup (ULONG ulReason)
{
    /*	Do whatever you need to do here to terminate
    */

    DosExitList(EXLST_EXIT, cleanup);
    return ;
}

#endif
#endif

#elif defined(TARG_WIN32)

#define WIN32_LEAN_AND_MEAN
#include <windows.h>
#include <apctype.h>

BOOL __stdcall DllMain (HANDLE hInst, ULONG ulReason, PVOID pReserved)
{
    switch (ulReason)
    {
	case DLL_PROCESS_ATTACH:
	    DllVisibles (1);
	    break;

	case DLL_PROCESS_DETACH:
	    DllVisibles (0);
	    break;

    }
    return TRUE;
}

#else

#error Must define either TARG_OS2 or TARG_WIN32

#endif
