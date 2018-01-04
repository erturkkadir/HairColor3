package com.syshuman.kadir.haircolor3.eventbus;
import com.syshuman.kadir.haircolor3.model.VisitM;

import java.util.List;

public class VisitEvents {

    public static class onVisitCreated {
        public VisitM visitM;
        public onVisitCreated(VisitM visitM) {
            this.visitM = visitM;
        }
    }

    public static class onVisitRead {
        public List<VisitM> visitMList;
        public onVisitRead(List<VisitM> visitMList) {
            this.visitMList = visitMList;
        }
    }

    public static class onVisitUpdated {
        public VisitM visitM;
        public onVisitUpdated(VisitM visitM) {
            this.visitM = visitM;
        }
    }

    public static class onVisitDeleted {
        public VisitM visitM;
        public onVisitDeleted(VisitM visitM) {
            this.visitM = visitM;
        }
    }
}
