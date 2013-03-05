package visualiser;
/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 



/*
 * TableSortDemo.java requires no other files.
 */

import generators.IGenerator;

import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;

import models.MyAgent;
import models.OptResult;
import storage.IStorage;
import util.CommonMethod;

@SuppressWarnings("serial")
public class CommunityStatsTable extends JPanel {

    public CommunityStatsTable(MyAgent[] agents, String optID) {
        super(new GridLayout(1,0));

        JTable table = new JTable(new CommunityInfoTable(agents, optID));
        table.setPreferredScrollableViewportSize(new Dimension(500, 70));
        table.setFillsViewportHeight(true);
        table.setAutoCreateRowSorter(true);

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        add(scrollPane);
    }

    class CommunityInfoTable extends AbstractTableModel {
        private String[] columnNames = {"Agent ID", "generation", "util", "charge","discharge", "min(stored)", "max(stored)", "waste", "link"};
        								
        Object[][] data;
        public CommunityInfoTable(MyAgent[] agents, String optID){
            
        	this.data = new Object[agents.length+1][columnNames.length];
        	double sum[] = new double[columnNames.length];
        	
        	for(int row=1; row<=agents.length; row++)
        		{
        			MyAgent agent = agents[row-1];
        			double generation = 0;
        			
        			for(IGenerator g:agent.getGenerators())
        					generation += CommonMethod.sum(g.getGeneration());
        			
        			IStorage storage = agent.getStorage().get(0);
        			OptResult optInfo = agent.getOptInfo(optID);
        			data[row][0] = agent.getID();
        			data[row][1] = generation;
        			data[row][2] = optInfo.getObjValue();
        			data[row][3] = CommonMethod.sum(optInfo.getChargeMapping().get(storage));
        			data[row][4] = CommonMethod.sum(optInfo.getDischargeMapping().get(storage));
        			data[row][5] = CommonMethod.min(optInfo.getStoredEnergyMapping().get(storage));
					data[row][6] = CommonMethod.max(optInfo.getStoredEnergyMapping().get(storage));
        			data[row][7] = CommonMethod.sum(optInfo.getWaste());
        			data[row][8] = CommonMethod.sum(optInfo.getLink());
        			
        			sum[1] += (double)data[row][1];
        			sum[2] += (double)data[row][2];
        			sum[3] += (double)data[row][3];
        			sum[4] += (double)data[row][4];
        			sum[5] += (double)data[row][5];
        			sum[6] += (double)data[row][6];
        			sum[7] += (double)data[row][7];
        			sum[8] += (double)data[row][8];
        			
        		}
        	
        	data[0][0] = "Sum";
        	data[0][1] = sum[1];
        	data[0][2] = sum[2];
        	data[0][3] = sum[3];
        	data[0][4] = sum[4];
        	data[0][5] = sum[5];
        	data[0][6] = sum[6];
        	data[0][7] = sum[7];
        	data[0][8] = sum[8];
        	}

		public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return data.length;
        }

        public String getColumnName(int col) {
            return columnNames[col];
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
		public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
        	return false;
        }

        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
 
        }
    }

}