#include <windows.h>
#include <stdlib.h>
#include <apctype.h>
#include "demo.h"

HANDLE hInst;			   /* current instance */

int usNRlen = 30;		   /* NREV length		  */
int usNRcnt = 100;		   /* NREV count		  */
long ulNRlips;			   /* NREV LIPS result		  */
char achNRtime[10];		   /* NREV Time result		  */

char achZtime[10];		   /* Zebra time result 	  */
char achZhouse1[80];		   /* Five zebra results	  */
char achZhouse2[80];
char achZhouse3[80];
char achZhouse4[80];
char achZhouse5[80];

/* Symbol must be defined and initialized */
int LOAD_ARITY = 1;

/* Prolog Entrypoints */
int SYSAPI NrevBenchmark (int usLength, int usIters);
int SYSAPI Zebra (VOID);
int SYSAPI LBStrings (char **pString, long *pRef);
long SYSAPI StoreString (char *pChar);
void SYSAPI DeleteString (long lRef);
void SYSAPI SaveDatabase (void);
void SYSAPI Torture (HWND hwnd);
BOOL TortureDisplay (HWND hwnd, LONG lIter, short nCache);
char * SYSAPI Sreverse (char *);

/* Function Prototypes */
int WINAPI WinMain(HANDLE hInstance, HANDLE hPrevInstance,
	    LPSTR lpCmdLine, int nCmdShow);
BOOL InitApplication (HANDLE hInstance);
BOOL InitInstance (HANDLE hInstance, int nCmdShow);
LRESULT SYSAPI DemoWndProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam);
BOOL SYSAPI AboutDlgProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam);
BOOL SYSAPI NrevDlgProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam);
BOOL SYSAPI Nrev2DlgProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam);
BOOL SYSAPI ZebraDlgProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam);
BOOL SYSAPI DynDlgProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam);
int FillListBox (HWND hwndListBox);
VOID GrayMenu (HWND hwnd, BOOL fGray);

int WINAPI WinMain(HANDLE hInstance, HANDLE hPrevInstance,
	    LPSTR lpCmdLine, int nCmdShow)
{
MSG msg;				     /* message			     */

    if (!InitApplication(hInstance)) /* Initialize shared things */
	return (FALSE);		 /* Exits if unable to initialize     */

    if (!InitInstance(hInstance, nCmdShow))
        return (FALSE);

    while (GetMessage(&msg, NULL, 0, 0))
	{
	TranslateMessage(&msg);
	DispatchMessage(&msg);
	}

    endprolog ();

    return (msg.wParam);
}

BOOL InitApplication (HANDLE hInstance)
{
WNDCLASS wc;

    wc.style = CS_HREDRAW | CS_VREDRAW;
    wc.lpfnWndProc = DemoWndProc;
    wc.cbClsExtra = 0;
    wc.cbWndExtra = 0;
    wc.hInstance = hInstance;
    wc.hIcon = LoadIcon(hInstance, "Lion");
    wc.hCursor = LoadCursor(NULL, IDC_ARROW);
    wc.hbrBackground = GetStockObject(WHITE_BRUSH);
    wc.lpszMenuName =  "DemoMenu";
    wc.lpszClassName = "DemoClass";

    return (RegisterClass(&wc));
}

BOOL InitInstance (HANDLE hInstance, int nCmdShow)
{
HWND hWnd;

    hInst = hInstance;

    if (initprolog (0, 0L, 0L))
	return FALSE;

    hWnd = CreateWindow(
	"DemoClass",
	"Demo Win32 App",
        WS_OVERLAPPEDWINDOW,            /* Window style.                      */
        CW_USEDEFAULT,                  /* Default horizontal position.       */
        CW_USEDEFAULT,                  /* Default vertical position.         */
        CW_USEDEFAULT,                  /* Default width.                     */
        CW_USEDEFAULT,                  /* Default height.                    */
        NULL,                           /* Overlapped windows have no parent. */
        NULL,                           /* Use the window class menu.         */
        hInstance,                      /* This instance owns this window.    */
        NULL                            /* Pointer not needed.                */
    );

    if (!hWnd)
        return (FALSE);

    ShowWindow(hWnd, nCmdShow);
    UpdateWindow(hWnd);
    return (TRUE);
}

LRESULT SYSAPI DemoWndProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam)
{
LPSTR lpText;
PAINTSTRUCT ps;
HDC hDC;
HCURSOR hCursor;
int n;

    switch (msg)
	{
	case WM_DESTROY:
	    PostQuitMessage(0);
	    break;

	case WM_COMMAND:
	    switch (wParam)
		{
		case IDM_ABOUT:
		    DialogBox(hInst, "AboutBox", hwnd, AboutDlgProc);
		    break;

		case IDM_DYNAMIC:
		    DialogBox(hInst, "Dynamic", hwnd, DynDlgProc);
		    break;

		case IDM_NREV:
		    n = DialogBox(hInst, "Nrev", hwnd, NrevDlgProc);
		    if (n)
			{
			hCursor = SetCursor (LoadCursor(NULL, IDC_WAIT));
			ShowCursor (TRUE);
			NrevBenchmark (usNRlen, usNRcnt);
			ShowCursor (FALSE);
			SetCursor (hCursor);
			DialogBox(hInst, "Nrev2", hwnd, Nrev2DlgProc);
			}
		    break;

		case IDM_ZEBRA:
		    hCursor = SetCursor (LoadCursor(NULL, IDC_WAIT));
		    ShowCursor (TRUE);
		    Zebra ();
		    ShowCursor (FALSE);
		    SetCursor (hCursor);
		    DialogBox(hInst, "Zebra", hwnd, ZebraDlgProc);
		    break;

		case IDM_STARTTORTURE:
		    GrayMenu (hwnd, TRUE);
		    Torture (hwnd);
		    GrayMenu (hwnd, FALSE);
		    break;

		}
		break;

	    default:
		return DefWindowProc (hwnd, msg, wParam, lParam);
	}
    return 0L;
}

BOOL SYSAPI AboutDlgProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam)
{
    switch (msg)
	{
	case WM_INITDIALOG:
	    return TRUE;

	case WM_COMMAND:
	    if (wParam == IDOK || wParam == IDCANCEL)
		{
		EndDialog (hwnd, TRUE);
		return TRUE;
		}
	    break;
	}
    return FALSE;
}

BOOL SYSAPI NrevDlgProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam)
{
BOOL fTrans;
int n;

    switch (msg)
	{
	case WM_INITDIALOG:
	    SetDlgItemInt (hwnd, ID_NREV_LEN, usNRlen, FALSE);
	    SetDlgItemInt (hwnd, ID_NREV_CNT, usNRcnt, FALSE);
	    return TRUE;

	case WM_COMMAND:
	    switch (wParam)
		{
		case IDOK:
		    n = GetDlgItemInt (hwnd, ID_NREV_LEN, &fTrans, FALSE);
		    if (!fTrans)
			{
			SetFocus (GetDlgItem (hwnd, ID_NREV_LEN));
			break;
			}
		    usNRlen = n;
		    n = GetDlgItemInt (hwnd, ID_NREV_CNT, &fTrans, FALSE);
		    if (!fTrans)
			{
			SetFocus (GetDlgItem (hwnd, ID_NREV_CNT));
			break;
			}
		    usNRcnt = n;
		    EndDialog (hwnd, TRUE);
		    break;

		case IDCANCEL:
		    EndDialog (hwnd, FALSE);
		    break;
		}
	    return TRUE;
	}
    return FALSE;
}

BOOL SYSAPI Nrev2DlgProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam)
{
static char achBuffer[20];

    switch (msg)
	{
	case WM_INITDIALOG:
	    SetDlgItemInt (hwnd, ID_NREV2_LEN, usNRlen, FALSE);
	    SetDlgItemInt (hwnd, ID_NREV2_CNT, usNRcnt, FALSE);
	    ltoa (ulNRlips, achBuffer, 10);
	    SetDlgItemText (hwnd, ID_NREV2_LIPS, achBuffer);
	    SetDlgItemText (hwnd, ID_NREV2_TIME, achNRtime);
	    return TRUE;

	case WM_COMMAND:
	    if (wParam == IDOK || wParam == IDCANCEL)
		{
		EndDialog (hwnd, TRUE);
		return TRUE;
		}
	    break;
	}

    return FALSE;
}

BOOL SYSAPI ZebraDlgProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam)
{
    switch (msg)
	{
	case WM_INITDIALOG:
	    SetDlgItemText (hwnd, ID_ZHOUSE1, achZhouse1);
	    SetDlgItemText (hwnd, ID_ZHOUSE2, achZhouse2);
	    SetDlgItemText (hwnd, ID_ZHOUSE3, achZhouse3);
	    SetDlgItemText (hwnd, ID_ZHOUSE4, achZhouse4);
	    SetDlgItemText (hwnd, ID_ZHOUSE5, achZhouse5);
	    SetDlgItemText (hwnd, ID_ZTIME, achZtime);
	    return TRUE;

	case WM_COMMAND:
	    if (wParam == IDOK || wParam == IDCANCEL)
		{
		EndDialog (hwnd, TRUE);
		return TRUE;
		}
	    break;
	}

    return FALSE;
}

BOOL SYSAPI DynDlgProc(HWND hwnd, UINT msg, WPARAM wParam, LPARAM lParam)
{
static HWND hwndListBox, hwndAddButton, hwndDeleteButton,
	    hwndTextField, hwndReverseButton;

int nSel, nSel1;
char *pChar;
BOOL f;

    switch (msg)
	{
	case WM_INITDIALOG:
	    hwndListBox = GetDlgItem (hwnd, ID_LISTBOX);
	    hwndAddButton = GetDlgItem (hwnd, ID_LBADD);
	    hwndDeleteButton = GetDlgItem (hwnd, ID_LBDELETE);
	    hwndReverseButton = GetDlgItem (hwnd, ID_LBREVERSE);
	    hwndTextField = GetDlgItem (hwnd, ID_LBTEXT);
	    nSel = FillListBox (hwndListBox);
	    EnableWindow(hwndDeleteButton,  nSel > 0);
	    EnableWindow(hwndAddButton, FALSE);
	    EnableWindow(hwndReverseButton, FALSE);
	    if (nSel > 0)
		SendMessage (hwndListBox, LB_SETCURSEL, 0, 0L);
	    return TRUE;

	case WM_COMMAND:
	    switch (LOWORD(wParam))
		{
		case ID_LBTEXT:
		    if (HIWORD(wParam) == EN_CHANGE)
			{
			f = SendMessage (hwndTextField, WM_GETTEXTLENGTH,
				0, 0L) > 0;
			EnableWindow (hwndAddButton, f);
			EnableWindow (hwndReverseButton, f);
			}
		    return 0L;

		case IDOK:
		    EndDialog (hwnd, 0);
		    return 0L;

		case ID_LBADD:
		    pChar = (char *) LocalAlloc (LMEM_FIXED,
			GetWindowTextLength (hwndTextField) + 1);
		    GetWindowText (hwndTextField, pChar, -1);
		    nSel = (WORD) SendMessage (hwndListBox, LB_ADDSTRING,
			0, (LONG)(LPSTR)pChar);
		    SendMessage (hwndListBox, LB_SETITEMDATA, nSel,
			    StoreString (pChar));
		    LocalFree ((HANDLE)pChar);
		    if (nSel == 0)
			{
			SendMessage (hwndListBox, LB_SETCURSEL, nSel, 0L);
			EnableWindow (hwndDeleteButton, TRUE);
			}
		    return 0L;

		case ID_LBREVERSE:
		    pChar = (char *) LocalAlloc (LMEM_FIXED,
			GetWindowTextLength (hwndTextField) + 1);
		    GetWindowText (hwndTextField, pChar, -1);
		    Sreverse(pChar);
		    nSel = (WORD) SendMessage (hwndListBox, LB_ADDSTRING,
			0, (LONG)(LPSTR)pChar);
		    SendMessage (hwndListBox, LB_SETITEMDATA, nSel,
			    StoreString (pChar));
		    LocalFree ((HANDLE)pChar);
		    if (nSel == 0)
			{
			SendMessage (hwndListBox, LB_SETCURSEL, nSel, 0L);
			EnableWindow (hwndDeleteButton, TRUE);
			}
		    return 0L;

		case ID_LBDELETE:
		    nSel = SendMessage (hwndListBox, LB_GETCURSEL, 0, 0L);
		    DeleteString (SendMessage (hwndListBox, LB_GETITEMDATA,
				nSel, 0L));
		    nSel1 = SendMessage (hwndListBox, LB_DELETESTRING, nSel, 0L);
		    if (nSel1 == 0)
			EnableWindow (hwndDeleteButton, FALSE);
		    else
			SendMessage (hwndListBox, LB_SETCURSEL,
			    nSel1 > nSel ? nSel : nSel-1, 0L);
		    return 0L;

		case ID_LBSAVE:
		    SaveDatabase ();
		    return 0L;

		}
	    break;

	}
    return FALSE;
}

int FillListBox (HWND hwndListBox)
{
int iSuccess;
char *pChar;
long lRef;
int iCount;

    SendMessage (hwndListBox, LB_RESETCONTENT, 0, 0L);
    iCount = 0;
    for (iSuccess = LBStrings (&pChar, &lRef); iSuccess; iSuccess = redo())
	{
	iCount += 1;
	SendMessage (hwndListBox, LB_SETITEMDATA,
	    (WORD)SendMessage (hwndListBox, LB_ADDSTRING, 0, (LONG)(LPSTR)pChar),
	    lRef);
	LocalFree ((HANDLE)pChar);
	}
    return iCount;
}

VOID GrayMenu (HWND hwnd, BOOL fGray)
{
HMENU hMenu;
WORD wGray, wGray1;

    hMenu = GetMenu (hwnd);
    wGray = fGray ? MF_GRAYED : MF_ENABLED;
    wGray1 = fGray ? MF_ENABLED : MF_GRAYED;

    EnableMenuItem (hMenu, 0, wGray | MF_BYPOSITION);
    EnableMenuItem (hMenu, 1, wGray | MF_BYPOSITION);
    EnableMenuItem (hMenu, IDM_ABOUT, wGray | MF_BYCOMMAND);
    EnableMenuItem (hMenu, IDM_NREV, wGray | MF_BYCOMMAND);
    EnableMenuItem (hMenu, IDM_ZEBRA, wGray | MF_BYCOMMAND);
    EnableMenuItem (hMenu, IDM_DYNAMIC, wGray | MF_BYCOMMAND);
    EnableMenuItem (hMenu, IDM_STARTTORTURE, wGray | MF_BYCOMMAND);
    EnableMenuItem (hMenu, IDM_ENDTORTURE, wGray1 | MF_BYCOMMAND);
    DrawMenuBar (hwnd);
}

BOOL TortureDisplay (HWND hwnd, LONG lIter, short nCache)
{
MSG msg;
char achMsg [60];
HDC hdc;
RECT rect;

    if (PeekMessage (&msg, hwnd, 0, 0, PM_REMOVE))
	{
	TranslateMessage(&msg);
	DispatchMessage(&msg);
	if (msg.message == WM_COMMAND)
	    {
	    InvalidateRect (hwnd, NULL, TRUE);
	    return FALSE;
	    }
	}

    wsprintf (achMsg, "Torture Test: %lu   Cache size: %u", lIter, nCache);
    hdc = GetDC (hwnd);
    GetClientRect (hwnd, &rect);
    DrawText (hdc, achMsg, -1, &rect,
	DT_CENTER | DT_VCENTER | DT_SINGLELINE);
    ReleaseDC (hwnd, hdc);
    return TRUE;
}
