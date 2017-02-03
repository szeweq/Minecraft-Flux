package szewek.mcflux.util;

import szewek.mcflux.util.error.ErrMsg;

import java.util.HashSet;

public enum ErrorReport {
	;
	private static HashSet<ErrMsg> errMsgs = new HashSet<>();

	public static void addErrMsg(ErrMsg em) {
		if (errMsgs.add(em)) {
			em.addUp();
		} else {
			int hc = em.hashCode();
			for (ErrMsg xem : errMsgs) {
				if (hc == xem.hashCode()) {
					xem.addUp();
					return;
				}
			}
		}
	}
}
