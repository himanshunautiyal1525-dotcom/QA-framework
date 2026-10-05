package org.smbc.datadiff.report;

public final class StyleRenderer {
    public void append(StringBuilder html) {
        html.append("""
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <meta name="viewport"
                          content="width=device-width, initial-scale=1.0">
                    <title>Data Reconciliation Report</title>
                    <style>
                        * {
                            box-sizing: border-box;
                        }

                        body {
                            margin: 0;
                            padding: 0;
                            font-family: Arial, Helvetica, sans-serif;
                            background: #f5f7fa;
                            color: #1f2937;
                        }

                        .container {
                            max-width: 1400px;
                            margin: 0 auto;
                            padding: 30px;
                        }

                        .header {
                            background: #1f2937;
                            color: white;
                            padding: 25px 30px;
                            border-radius: 8px;
                            margin-bottom: 25px;
                        }

                        .header h1 {
                            margin: 0 0 8px 0;
                            font-size: 28px;
                        }

                        .header p {
                            margin: 0;
                            color: #d1d5db;
                        }

                        .summary-grid {
                            display: grid;
                            grid-template-columns:
                                repeat(auto-fit, minmax(180px, 1fr));
                            gap: 15px;
                            margin-bottom: 30px;
                        }

                        .summary-card {
                            background: white;
                            border-radius: 8px;
                            padding: 20px;
                            box-shadow:
                                0 1px 3px rgba(0,0,0,0.08);
                        }

                        .summary-card .label {
                            font-size: 13px;
                            color: #6b7280;
                            margin-bottom: 8px;
                        }

                        .summary-card .value {
                            font-size: 28px;
                            font-weight: bold;
                        }

                        .matched {
                            border-left: 5px solid #16a34a;
                        }

                        .mismatched {
                            border-left: 5px solid #dc2626;
                        }

                        .error {
                            border-left: 5px solid #d97706;
                        }

                        .file-section {
                            background: white;
                            border-radius: 8px;
                            margin-bottom: 25px;
                            box-shadow:
                                0 1px 3px rgba(0,0,0,0.08);
                            overflow: hidden;
                        }

                        .file-header {
                            padding: 18px 22px;
                            background: #f9fafb;
                            border-bottom: 1px solid #e5e7eb;
                        }

                        .file-header h2 {
                            margin: 0 0 8px 0;
                            font-size: 20px;
                        }

                        .status {
                            display: inline-block;
                            padding: 5px 10px;
                            border-radius: 20px;
                            font-size: 12px;
                            font-weight: bold;
                        }

                        .status-match {
                            background: #dcfce7;
                            color: #166534;
                        }

                        .status-mismatch {
                            background: #fee2e2;
                            color: #991b1b;
                        }

                        .status-error {
                            background: #fef3c7;
                            color: #92400e;
                        }

                        .file-body {
                            padding: 22px;
                        }

                        .metrics {
                            display: grid;
                            grid-template-columns:
                                repeat(auto-fit, minmax(150px, 1fr));
                            gap: 12px;
                            margin-bottom: 25px;
                        }

                        .metric {
                            background: #f9fafb;
                            padding: 15px;
                            border-radius: 6px;
                        }

                        .metric-label {
                            font-size: 12px;
                            color: #6b7280;
                            margin-bottom: 5px;
                        }

                        .metric-value {
                            font-size: 20px;
                            font-weight: bold;
                        }

                        .section {
                            margin-top: 25px;
                        }

                        .section h3 {
                            font-size: 16px;
                            margin-bottom: 12px;
                            padding-bottom: 8px;
                            border-bottom: 1px solid #e5e7eb;
                        }

                        .record {
                            border: 1px solid #e5e7eb;
                            border-radius: 6px;
                            margin-bottom: 12px;
                            overflow: hidden;
                        }

                        .record-header {
                            background: #f9fafb;
                            padding: 10px 14px;
                            font-weight: bold;
                        }

                        table {
                            width: 100%;
                            border-collapse: collapse;
                        }

                        th {
                            background: #f3f4f6;
                            text-align: left;
                            padding: 10px;
                            font-size: 13px;
                        }

                        td {
                            padding: 10px;
                            border-top: 1px solid #e5e7eb;
                            font-size: 13px;
                            vertical-align: top;
                        }

                        .actual {
                            color: #991b1b;
                        }

                        .expected {
                            color: #166534;
                        }

                        .empty-value {
                            color: #9ca3af;
                            font-style: italic;
                        }

                        .record-error {
                            background: #fff7ed;
                            border: 1px solid #fed7aa;
                            color: #9a3412;
                            padding: 14px;
                            border-radius: 6px;
                            margin-bottom: 12px;
                        }

                        .record-error-header {
                            font-weight: bold;
                            margin-bottom: 6px;
                        }

                        .record-error-meta {
                            color: #7c2d12;
                            font-size: 12px;
                            margin-bottom: 6px;
                        }

                        .error-message {
                            background: #fff7ed;
                            border: 1px solid #fed7aa;
                            color: #9a3412;
                            padding: 15px;
                            border-radius: 6px;
                        }

                        .footer {
                            text-align: center;
                            color: #6b7280;
                            font-size: 12px;
                            padding: 20px 0;
                        }


                        .collapsible {
                            margin-top: 18px;
                        }

                        .collapsible > summary {
                            cursor: pointer;
                            list-style: none;
                            padding: 12px 14px;
                            background: #f9fafb;
                            border: 1px solid #e5e7eb;
                            border-radius: 6px;
                            font-weight: bold;
                            user-select: none;
                        }

                        .collapsible > summary::-webkit-details-marker {
                            display: none;
                        }

                        .collapsible > summary::before {
                            content: "▶";
                            display: inline-block;
                            margin-right: 8px;
                            font-size: 11px;
                            transition: transform 0.15s ease;
                        }

                        .collapsible[open] > summary::before {
                            transform: rotate(90deg);
                        }

                        .collapsible-content {
                            padding-top: 12px;
                        }

                        .record-details {
                            margin-bottom: 12px;
                        }

                        .record-details > summary {
                            display: flex;
                            align-items: center;
                            justify-content: space-between;
                            gap: 12px;
                        }

                        .record-count {
                            color: #6b7280;
                            font-size: 12px;
                            font-weight: normal;
                        }

                        .file-summary {
                            display: flex;
                            align-items: center;
                            justify-content: space-between;
                            gap: 15px;
                            width: 100%;
                        }

                        .file-summary-name {
                            font-size: 20px;
                        }

                        .file-summary-meta {
                            display: flex;
                            align-items: center;
                            gap: 10px;
                            flex-wrap: wrap;
                        }

                        .file-body {
                            padding: 22px;
                            border-top: 1px solid #e5e7eb;
                        }

                        @media (max-width: 700px) {
                            .container {
                                padding: 15px;
                            }

                            .file-body {
                                padding: 15px;
                            }

                            table {
                                display: block;
                                overflow-x: auto;
                            }
                        }
                    </style>
                </head>
                <body>
                <div class="container">
                """);
    
    }
}
