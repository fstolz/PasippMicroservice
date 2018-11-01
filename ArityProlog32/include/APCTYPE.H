#if defined(TARG_WIN32)
    #ifndef SYSAPI
    #define SYSAPI __stdcall
    #endif
    #if defined(ARITY32_DLL)
	#define DLLIMP
    #else
	#define DLLIMP __declspec(dllimport)
    #endif
#elif defined(TARG_OS2)
    #ifndef SYSAPI
    #define SYSAPI _System
    #endif
    #define DLLIMP
#else
    #error Must define either TARG_WIN32 or TARG_OS2
#endif

#define SUCCESS 1
#define FAILURE 0

/* Flag values for PrologInitProcess */

#define     IP_ENVFILE	    1
#define     IP_ENVSTRING    2
#define     IP_STRINGDEF    4
#define     IP_SINGLETHREAD 8
#define     IP_ENVDEFAULT   0

/* Flag values for PrologInitThread */

#define     IT_FILLAREA     01


/* Event types */

#define     EV_FILEERROR    1
#define     EV_EXERROR	    2
#define     EV_ABORT	    3
#define     EV_HALT	    4
#define     EV_POISON	    5

/* Return values from event handlers */

#define     EVR_ENDPROCESS  0
#define     EVR_ENDTHREAD   1
#define     EVR_CONTINUE    2

typedef struct {
    int cbData; 			/* Size of data structure */
    unsigned long ulLocalSize;		/* Size of local in kilobyte */
    unsigned long ulGlobalSize; 	/* Size of global in kilobyts */
    unsigned long ulGlobalAlarm;	/* Garbage collection threshold */
    unsigned long ulLocalAlarm;		/* Garbage collection threshold */
} INITTHREAD;


/* Function prototypes */

int SYSAPI PrologInitProcess (int, void *, long);
DLLIMP int SYSAPI PrologEnvValue (char *, void *);
DLLIMP int SYSAPI PrologInitThread (int, INITTHREAD *);
DLLIMP int SYSAPI PrologEndThread (void);
DLLIMP void SYSAPI PrologEndProcess (void);
DLLIMP int SYSAPI PrologPoisonThread (int);
DLLIMP int SYSAPI PrologPoisonThread1 (int, int);

#if defined(TARG_OS2)
typedef long (* SYSAPI PENVHDLR) (int, long, long);
#elif defined(TARG_WIN32)
typedef long (SYSAPI * PENVHDLR) (int, long, long);
#endif

DLLIMP PENVHDLR SYSAPI PrologRegEvHdlr (PENVHDLR);
DLLIMP long SYSAPI PrologDefEvHdlr (int, long, long);
DLLIMP char * SYSAPI PrologErrorMessage (int);


/* Single thread entrypoints */
int SYSAPI initprolog (int, void *, long);
DLLIMP void SYSAPI endprolog (void);

/* Embedded C functions */
DLLIMP int SYSAPI redo (void);
DLLIMP void SYSAPI cut (void);

void SYSAPI DllVisibles (int fEnable);
